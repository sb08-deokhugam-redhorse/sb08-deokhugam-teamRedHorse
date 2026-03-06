package com.redhorse.deokhugam.domain.book.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.redhorse.deokhugam.domain.book.entity.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static com.redhorse.deokhugam.domain.book.entity.QBook.book;

@RequiredArgsConstructor
@Repository
public class BookRepositoryCustomImpl implements BookRepositoryCustom
{
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 도서 목록을 커서 페이지네이션으로 조회한다.
     *
     * @param keyword   검색 키워드 (제목, 저자, ISBN)
     * @param orderBy   정렬 기준 (title, publishedDate, rating, reviewCount)
     * @param direction 정렬 방향 (ASC, DESC)
     * @param cursor    이전 페이지 마지막 요소의 정렬 기준 값 (1차 커서)
     * @param after     이전 페이지 마지막 요소의 createdAt (2차 커서)
     * @param limit     페이지 크기
     * @return 도서 목록과 다음 페이지 존재 여부
     */
    @Override
    public Slice<Book> getAllBooks(String keyword, String orderBy, String direction, String cursor, Instant after, int limit) {
        boolean isAsc = "ASC".equalsIgnoreCase(direction);

        List<Book> result = jpaQueryFactory
                .selectFrom(book)
                .where(
                        keywordCondition(keyword),                         // 검색
                        cursorPageCondition(orderBy, cursor, after, isAsc) // 페이지네이션
                )
                .orderBy(
                        orderByCondition(orderBy, isAsc),                    // 1차 정렬
                        isAsc ? book.createdAt.asc() : book.createdAt.desc() // 2차 정렬
                )
                .limit(limit+1)
                .fetch();

        boolean hasNext = result.size() > limit;
        if (hasNext) result.remove(limit);

        return new SliceImpl<>(result, Pageable.unpaged(), hasNext);
    }

    /**
     * 키워드 조건만 적용해 전체 도서 수를 반환한다.
     *
     * @param keyword 검색 키워드
     * @return 전체 도서 수
     */
    @Override
    public long countBooksWithKeyword(String keyword) {
        Long count = jpaQueryFactory
                .select(book.count())
                .from(book)
                .where(keywordCondition(keyword))
                .fetchOne();

        return count != null ? count : 0L;
    }

    /**
     * 키워드로 제목, 저자, ISBN을 대소문자 구분 없이 부분일치 검색한다.
     *
     * @param keyword 검색 키워드
     * @return QueryDSL 조건식
     */
    private BooleanExpression keywordCondition(String keyword) {
        if (keyword == null || keyword.isBlank()) return null;

        return book.title.containsIgnoreCase(keyword)
                .or(book.author.containsIgnoreCase(keyword))
                .or(book.isbn.containsIgnoreCase(keyword));
    }

    /**
     * 1차 커서(cursor)와 2차 커서(after)를 조합해 페이지네이션 조건을 생성한다.
     * cursor와 after가 둘 다 null이면 첫 페이지로 간주한다.
     *
     * @param orderBy 정렬 기준
     * @param cursor  1차 커서 값
     * @param after   2차 커서 값
     * @param isAsc   정렬 방향
     * @return QueryDSL 조건식
     */
    private BooleanExpression cursorPageCondition(String orderBy, String cursor, Instant after, boolean isAsc) {
        if (cursor == null || after == null) return null;

        BooleanExpression primaryEq;   // 일치
        BooleanExpression primaryGtLt; // 대소

        switch (orderBy == null ? "title" : orderBy) {
            case "publishedDate" -> {
                LocalDate date = LocalDate.parse(cursor);
                primaryEq = book.publishedDate.eq(date);
                primaryGtLt = isAsc ? book.publishedDate.gt(date) : book.publishedDate.lt(date);
            }
            case "rating" -> {
                Double value = Double.parseDouble(cursor);
                primaryEq = book.rating.eq(value);
                primaryGtLt = isAsc ? book.rating.gt(value) : book.rating.lt(value);
            }
            case "reviewCount" -> {
                Long value = Long.parseLong(cursor);
                primaryEq = book.reviewCount.eq(value);
                primaryGtLt = isAsc ? book.reviewCount.gt(value) : book.reviewCount.lt(value);
            }
            default -> {
                primaryEq = book.title.eq(cursor);
                primaryGtLt = isAsc ? book.title.gt(cursor) : book.title.lt(cursor);
            }
        }

        BooleanExpression secondaryGtLt = isAsc ? book.createdAt.gt(after) : book.createdAt.lt(after);

        return primaryGtLt.or(primaryEq.and(secondaryGtLt));
    }

    /**
     * 정렬 기준과 방향으로 QueryDSL OrderSpecifier를 생성한다.
     *
     * @param orderBy 정렬 기준
     * @param isAsc 정렬 방향
     * @return 정렬 조건
     */
    private OrderSpecifier<?> orderByCondition(String orderBy, boolean isAsc) {
        return switch (orderBy == null ? "title" : orderBy) {
            case "publishedDate" -> isAsc ? book.publishedDate.asc() : book.publishedDate.desc();
            case "rating" -> isAsc ? book.rating.asc() : book.rating.desc();
            case "reviewCount" -> isAsc ? book.reviewCount.asc() : book.reviewCount.desc();
            default -> isAsc ? book.title.asc() : book.title.desc();
        };
    }
}
