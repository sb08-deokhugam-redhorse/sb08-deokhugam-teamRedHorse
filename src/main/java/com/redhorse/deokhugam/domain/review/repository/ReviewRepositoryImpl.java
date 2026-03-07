package com.redhorse.deokhugam.domain.review.repository;

import static com.redhorse.deokhugam.domain.review.entity.QReview.review;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.redhorse.deokhugam.domain.review.dto.ReviewSearchRequest;
import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.global.exception.InvalidCursorException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Slice<Review> getAllReviews(ReviewSearchRequest request) {

    int limit = (request.limit() == null || request.limit() < 1) ? 50 : request.limit();
    String orderBy = request.orderBy() != null ? request.orderBy() : "createdAt";
    boolean isAsc = "ASC".equalsIgnoreCase(request.direction());

    List<Review> find = jpaQueryFactory
        .selectFrom(review)
        .join(review.book).fetchJoin()
        .join(review.user).fetchJoin()
        .where(
            review.deletedAt.isNull(),
            keywordCondition(request.keyword()),
            bookIdCondition(request.bookId()),
            userIdCondition(request.userId()),
            cursorCondition(orderBy, isAsc, request.cursor(), request.after())
        )
        .orderBy(
            orderByCondition(orderBy, isAsc),
            isAsc ? review.createdAt.asc() : review.createdAt.desc()
        )
        .limit(limit + 1)
        .fetch();

    boolean hasNext = find.size() > limit;
    if (hasNext) {
      find.remove(limit);
    }

    return new SliceImpl<>(find, Pageable.unpaged(), hasNext);
  }

  // 키워드 검색 - 부분 일치
  private BooleanExpression keywordCondition(String keyword) {
    if (keyword == null || keyword.isBlank()) {
      return null;
    }

    return review.user.nickname.containsIgnoreCase(keyword)
        .or(review.content.containsIgnoreCase(keyword))
        .or(review.book.title.containsIgnoreCase(keyword));
  }

  // bookId - 완전 일치
  private BooleanExpression bookIdCondition(UUID bookId) {
    return bookId != null ? review.book.id.eq(bookId) : null;
  }

  // userId - 완전 일치
  private BooleanExpression userIdCondition(UUID userId) {
    return userId != null ? review.user.id.eq(userId) : null;
  }

  // 페이지네이션
  private BooleanExpression cursorCondition(String orderBy, boolean isAsc, String cursor,
      Instant after) {
    if (cursor == null || after == null) {
      return null;
    }

    BooleanExpression primaryEq;
    BooleanExpression primaryGtLt;
    BooleanExpression secondaryGtLt;

    try {
      switch (orderBy) {
        case "rating" -> {
          int rating = Integer.parseInt(cursor);
          primaryEq = review.rating.eq(rating);
          primaryGtLt = isAsc ? review.rating.gt(rating) : review.rating.lt(rating);
        }
        default -> {
          Instant createAt = Instant.parse(cursor);
          primaryEq = review.createdAt.eq(createAt);
          primaryGtLt = isAsc ? review.createdAt.gt(createAt) : review.createdAt.lt(createAt);
        }
      }

    } catch (Exception e) {
      throw new InvalidCursorException(cursor);
    }

    secondaryGtLt = isAsc ? review.createdAt.gt(after) : review.createdAt.lt(after);
    return primaryGtLt.or(primaryEq.and(secondaryGtLt));
  }

  // 정렬
  private OrderSpecifier<?> orderByCondition(String orderBy, boolean isAsc) {
    if ("rating".equals(orderBy)) {
      return isAsc ? review.rating.asc() : review.rating.desc();
    }
    return isAsc ? review.createdAt.asc() : review.createdAt.desc();
  }


  @Override
  public long getTotal(ReviewSearchRequest request) {
    long total = Optional.ofNullable(jpaQueryFactory
            .select(review.count())
            .from(review)
            .where(
                review.deletedAt.isNull(),
                keywordCondition(request.keyword()),
                bookIdCondition(request.bookId()),
                userIdCondition(request.userId())
            )
            .fetchOne())
        .orElse(0L);

    return total;
  }


}
