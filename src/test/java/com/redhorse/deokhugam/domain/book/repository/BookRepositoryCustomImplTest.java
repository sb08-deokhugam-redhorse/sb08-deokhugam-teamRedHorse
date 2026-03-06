package com.redhorse.deokhugam.domain.book.repository;

import com.redhorse.deokhugam.domain.book.entity.Book;
import com.redhorse.deokhugam.global.config.JpaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
@DisplayName("BookRepositoryCustomImpl Slice Test")
class BookRepositoryCustomImplTest
{
    @Autowired private BookRepositoryCustomImpl bookRepositoryCustomImpl;
    @Autowired private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository.saveAll(List.of(
                new Book("스프링 부트 핵심", "김스프링", "스프링 부트 마스터하기", "IT북스",
                        LocalDate.of(2024, 1, 1), "9788900000001", "url1", false, 4.8, 10L, new ArrayList<>()),
                new Book("자바의 정석", "남궁성", "자바의 교과서", "도우출판",
                        LocalDate.of(2024, 1, 15), "9788900000002", "url2", false, 4.5, 15L, new ArrayList<>()),
                new Book("클린 코드", "로버트 C. 마틴", "소프트웨어 장인정신", "인사이트",
                        LocalDate.of(2024, 2, 1), "9788900000003", "url3", false, 4.2, 5L, new ArrayList<>()),
                new Book("모던 자바스크립트", "이웅모", "JS 딥다이브", "위키북스",
                        LocalDate.of(2024, 2, 20), "9788900000004", "url4", false, 4.0, 8L, new ArrayList<>()),
                new Book("데이터베이스 개론", "이데이터", "DB 기초 다지기", "한빛미디어",
                        LocalDate.of(2024, 3, 1), "9788900000005", "url5", false, 4.9, 20L, new ArrayList<>())
        ));
    }

    @Nested
    @DisplayName("도서 목록 조회")
    class GetAllBooks {
        @Test
        @DisplayName("성공 - 키워드 없이 전체 조회한다")
        void success_withNoKeyword_returnsAll() {
            Slice<Book> result = bookRepositoryCustomImpl.getAllBooks(null, "title", "DESC", null, null, 10);

            assertThat(result.getContent()).hasSize(5);
            assertThat(result.hasNext()).isFalse();
        }

        @Test
        @DisplayName("성공 - 키워드로 제목 부분일치 검색한다")
        void success_withKeyword_filtersByTitle() {
            Slice<Book> result = bookRepositoryCustomImpl.getAllBooks("자바", "title", "DESC", null, null, 10);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent())
                    .extracting(Book::getTitle)
                    .containsExactlyInAnyOrder("자바의 정석", "모던 자바스크립트");
        }

        @Test
        @DisplayName("성공 - title DESC 정렬로 조회한다")
        void success_withTitleDesc_returnsSortedByTitleDesc() {
            Slice<Book> result = bookRepositoryCustomImpl.getAllBooks(null, "title", "DESC", null, null, 10);

            List<String> titles = result.getContent().stream().map(Book::getTitle).toList();
            assertThat(titles).isSortedAccordingTo(Comparator.reverseOrder());
        }

        @Test
        @DisplayName("성공 - limit보다 데이터가 많으면 hasNext가 true다")
        void success_withLimitLessThanTotal_hasNextIsTrue() {
            Slice<Book> result = bookRepositoryCustomImpl.getAllBooks(null, "title", "DESC", null, null, 3);

            assertThat(result.getContent()).hasSize(3);
            assertThat(result.hasNext()).isTrue();
        }

        @Test
        @DisplayName("성공 - 커서 페이지네이션으로 다음 페이지를 조회한다")
        void success_withCursor_returnsNextPage() {
            Slice<Book> firstPage = bookRepositoryCustomImpl.getAllBooks(null, "title", "DESC", null, null, 3);
            Book lastBook = firstPage.getContent().get(firstPage.getContent().size() - 1);

            Slice<Book> secondPage = bookRepositoryCustomImpl.getAllBooks(
                    null, "title", "DESC",
                    lastBook.getTitle(),
                    lastBook.getCreatedAt(),
                    3
            );

            assertThat(secondPage.getContent()).hasSize(2);
            assertThat(secondPage.hasNext()).isFalse();
        }

        @Test
        @DisplayName("성공 - 논리 삭제된 도서는 조회되지 않는다")
        void success_withDeletedBook_excludesDeleted() {
            Book deletedBook = new Book("삭제된 도서", "삭제저자", "삭제소개", "삭제출판사",
                    LocalDate.of(2024, 1, 1), "9788900000099", null, true, 0.0, 0L, new ArrayList<>());
            bookRepository.save(deletedBook);

            Slice<Book> result = bookRepositoryCustomImpl.getAllBooks(null, "title", "DESC", null, null, 10);

            assertThat(result.getContent()).hasSize(5);
            assertThat(result.getContent())
                    .extracting(Book::getTitle)
                    .doesNotContain("삭제된 도서");
        }

        @Test
        @DisplayName("성공 - publishedDate 정렬 및 커서 페이지네이션")
        void success_cursorPagination_byPublishedDate() {
            Slice<Book> firstPage = bookRepositoryCustomImpl.getAllBooks(null, "publishedDate", "DESC", null, null, 3);
            Book lastBook = firstPage.getContent().get(firstPage.getContent().size() - 1);

            Slice<Book> secondPage = bookRepositoryCustomImpl.getAllBooks(
                    null, "publishedDate", "DESC",
                    lastBook.getPublishedDate().toString(),
                    lastBook.getCreatedAt(), 3);

            assertThat(secondPage.getContent()).hasSize(2);
            assertThat(secondPage.hasNext()).isFalse();
        }

        @Test
        @DisplayName("성공 - rating 정렬 및 커서 페이지네이션")
        void success_cursorPagination_byRating() {
            Slice<Book> firstPage = bookRepositoryCustomImpl.getAllBooks(null, "rating", "DESC", null, null, 3);
            Book lastBook = firstPage.getContent().get(firstPage.getContent().size() - 1);

            Slice<Book> secondPage = bookRepositoryCustomImpl.getAllBooks(
                    null, "rating", "DESC",
                    lastBook.getRating().toString(),
                    lastBook.getCreatedAt(), 3);

            assertThat(secondPage.getContent()).hasSize(2);
            assertThat(secondPage.hasNext()).isFalse();
        }

        @Test
        @DisplayName("성공 - reviewCount 정렬 및 커서 페이지네이션")
        void success_cursorPagination_byReviewCount() {
            Slice<Book> firstPage = bookRepositoryCustomImpl.getAllBooks(null, "reviewCount", "DESC", null, null, 3);
            Book lastBook = firstPage.getContent().get(firstPage.getContent().size() - 1);

            Slice<Book> secondPage = bookRepositoryCustomImpl.getAllBooks(
                    null, "reviewCount", "DESC",
                    lastBook.getReviewCount().toString(),
                    lastBook.getCreatedAt(), 3);

            assertThat(secondPage.getContent()).hasSize(2);
            assertThat(secondPage.hasNext()).isFalse();
        }
    }

    @Nested
    @DisplayName("전체 도서 수")
    class CountBooksWithKeyword {
        @Test
        @DisplayName("성공 - 키워드 없이 전체 카운트를 반환한다")
        void success_withNoKeyword_returnsTotal() {
            long count = bookRepositoryCustomImpl.countBooksWithKeyword(null);

            assertThat(count).isEqualTo(5);
        }

        @Test
        @DisplayName("성공 - 키워드로 필터링된 카운트를 반환한다")
        void success_withKeyword_returnsFilteredCount() {
            long count = bookRepositoryCustomImpl.countBooksWithKeyword("자바");

            assertThat(count).isEqualTo(2);
        }
    }
}