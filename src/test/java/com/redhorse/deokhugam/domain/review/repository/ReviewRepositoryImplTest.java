package com.redhorse.deokhugam.domain.review.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.redhorse.deokhugam.domain.book.entity.Book;
import com.redhorse.deokhugam.domain.book.repository.BookRepository;
import com.redhorse.deokhugam.domain.review.dto.ReviewSearchRequest;
import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import com.redhorse.deokhugam.global.config.JpaConfig;
import com.redhorse.deokhugam.global.exception.InvalidCursorException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
@Import(JpaConfig.class)
@DisplayName("리뷰 목록 조회")
public class ReviewRepositoryImplTest {

  @Autowired
  ReviewRepository reviewRepository;

  @Autowired
  BookRepository bookRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  ReviewRepositoryImpl reviewRepositoryImpl;

  private UUID user1Id;

  private UUID book1Id;

  @BeforeEach
  void setUp() {

    User user1 = userRepository.save(new User("a@test.com", "user1", "pw"));
    User user2 = userRepository.save(new User("b@test.com", "user2", "pw"));
    User user3 = userRepository.save(new User("c@test.com", "user3", "pw"));

    Book book1 = bookRepository.save(
        new Book("title", "author", "description", "publisher", LocalDate.now(),
            "isbn1", "thumbnailUrl", false, 3.0, 3L, new ArrayList<>()));
    Book book2 = bookRepository.save(
        new Book("test", "user", "hello", "publisher", LocalDate.now(),
            "isbn2", "thumbnailUrl", false, 4.0, 9L, new ArrayList<>()));

    user1Id = user1.getId();
    book1Id = book1.getId();

    // 시간차 저장
    Instant base = Instant.now();

    Review r1 = new Review("test1", 1, book1, user1);
    ReflectionTestUtils.setField(r1, "createdAt", base.minusSeconds(6));
    Review r2 = new Review("test2", 2, book1, user2);
    ReflectionTestUtils.setField(r2, "createdAt", base.minusSeconds(5));
    Review r3 = new Review("test3", 3, book1, user3);
    ReflectionTestUtils.setField(r3, "createdAt", base.minusSeconds(4));
    Review r4 = new Review("test5", 1, book2, user1);
    ReflectionTestUtils.setField(r4, "createdAt", base.minusSeconds(3));
    Review r5 = new Review("test2", 4, book2, user2);
    ReflectionTestUtils.setField(r5, "createdAt", base.minusSeconds(2));
    Review r6 = new Review("test4", 2, book2, user3);
    ReflectionTestUtils.setField(r6, "createdAt", base.minusSeconds(1));

    reviewRepository.saveAll(List.of(r1, r2, r3, r4, r5, r6));
  }

  @Test
  @DisplayName("기본 조회")
  void defaultSearch() {
    // given
    ReviewSearchRequest request = new ReviewSearchRequest(null, null, null, null,
        null, null, null, null);

    // when
    Slice<Review> result = reviewRepositoryImpl.getAllReviews(request);

    // then
    assertThat(result.getContent()).hasSize(6);
    assertThat(result.hasNext()).isFalse();
  }

  @Test
  @DisplayName("키워드 검색 - 유저 닉네임")
  void keywordSearch_userNickname() {
    // given
    ReviewSearchRequest request = new ReviewSearchRequest(null, null, "eR1", null,
        null, null, null,
        null);

    // when
    Slice<Review> result = reviewRepositoryImpl.getAllReviews(request);

    // then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.hasNext()).isFalse();
    assertThat(result.getContent())
        .extracting(r -> r.getUser().getNickname())
        .containsOnly("user1");
  }

  @Test
  @DisplayName("키워드 검색 - 리뷰 내용")
  void keywordSearch_reviewContent() {
    // given
    ReviewSearchRequest request = new ReviewSearchRequest(null, null, "4", null,
        null, null, null, null);

    // when
    Slice<Review> result = reviewRepositoryImpl.getAllReviews(request);

    // then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.hasNext()).isFalse();
    assertThat(result.getContent())
        .extracting(Review::getContent)
        .containsOnly("test4");
  }

  @Test
  @DisplayName("키워드 검색 - 책 이름")
  void keywordSearch_bookTitle() {
    // given
    ReviewSearchRequest request = new ReviewSearchRequest(null, null, "Title", null,
        null, null, null, null);

    // when
    Slice<Review> result = reviewRepositoryImpl.getAllReviews(request);

    // then
    assertThat(result.getContent()).hasSize(3);
    assertThat(result.hasNext()).isFalse();
    assertThat(result.getContent())
        .extracting(r -> r.getBook().getTitle())
        .containsOnly("title");
  }

  @Test
  @DisplayName("특정 책만 검색")
  void bookSearch() {
    // given
    ReviewSearchRequest request = new ReviewSearchRequest(null, book1Id, null, null,
        null, null, null, null);

    // when
    Slice<Review> result = reviewRepositoryImpl.getAllReviews(request);

    // then
    assertThat(result.getContent()).hasSize(3);
    assertThat(result.hasNext()).isFalse();
    assertThat(result.getContent())
        .allMatch(r -> r.getBook().getId().equals(book1Id));
  }

  @Test
  @DisplayName("특정 사용자만 검색")
  void userSearch() {
    // given
    ReviewSearchRequest request = new ReviewSearchRequest(user1Id, null, null, null,
        null, null, null, null);

    // when
    Slice<Review> result = reviewRepositoryImpl.getAllReviews(request);

    // then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.hasNext()).isFalse();
    assertThat(result.getContent())
        .allMatch(r -> r.getUser().getId().equals(user1Id));
  }

  @Test
  @DisplayName("커서 페이지네이션 - 성공")
  void cursorPagination_success() {
    // given & when
    ReviewSearchRequest request1 = new ReviewSearchRequest(null, null, null, null,
        "asc", null, null, 3);
    Slice<Review> result1 = reviewRepositoryImpl.getAllReviews(request1);

    Review lastReview = result1.getContent().get(2);
    ReviewSearchRequest request2 = new ReviewSearchRequest(null, null, null, null,
        "asc", lastReview.getCreatedAt().toString(), lastReview.getCreatedAt(), 3);
    Slice<Review> result2 = reviewRepositoryImpl.getAllReviews(request2);

    // then
    assertThat(result1.getContent()).hasSize(3);
    assertThat(result1.hasNext()).isTrue();
    assertThat(result1.getContent())
        .extracting(Review::getCreatedAt)
        .isSorted();

    assertThat(result2.getContent()).hasSize(3);
    assertThat(result2.hasNext()).isFalse();
    assertThat(result2.getContent())
        .extracting(Review::getCreatedAt)
        .isSorted();

  }

  @Test
  @DisplayName("커서 페이지네이션 - rating 정렬")
  void cursorPagination_rating() {
    // given & when
    ReviewSearchRequest request1 = new ReviewSearchRequest(null, null, null, "rating",
        "asc", null, null, 3);
    Slice<Review> result1 = reviewRepositoryImpl.getAllReviews(request1);

    Review lastReview = result1.getContent().get(2);
    ReviewSearchRequest request2 = new ReviewSearchRequest(null, null, null, "rating",
        "asc", String.valueOf(lastReview.getRating()), lastReview.getCreatedAt(), 3);
    Slice<Review> result2 = reviewRepositoryImpl.getAllReviews(request2);

    // then
    assertThat(result1.getContent()).hasSize(3);
    assertThat(result1.hasNext()).isTrue();
    assertThat(result1.getContent())
        .extracting(Review::getRating)
        .isSorted();

    assertThat(result2.getContent()).hasSize(3);
    assertThat(result2.hasNext()).isFalse();
    assertThat(result2.getContent())
        .extracting(Review::getRating)
        .isSorted();
  }

  @Test
  @DisplayName("커서 페이지네이션 - 실패")
  void cursorPagination_failure() {
    // given
    ReviewSearchRequest request = new ReviewSearchRequest(null, null, null, null,
        "asc", "null", Instant.now(), 3);

    // when & then
    assertThrows(InvalidCursorException.class, () -> reviewRepositoryImpl.getAllReviews(request));
  }

  @Test
  @DisplayName("rating 정렬")
  void ratingSort() {
    // given
    ReviewSearchRequest request = new ReviewSearchRequest(null, null, null, "rating",
        null, null, null, null);

    // when
    Slice<Review> result = reviewRepositoryImpl.getAllReviews(request);

    // then
    assertThat(result.getContent()).hasSize(6);
    assertThat(result.hasNext()).isFalse();
    assertThat(result.getContent())
        .extracting(Review::getRating)
        .isSortedAccordingTo(Comparator.reverseOrder());
    assertThat(result.getContent())
        .extracting(Review::getContent)
        .containsExactly("test2", "test3", "test4", "test2", "test5", "test1");
  }

  @Test
  @DisplayName("total 리뷰 수")
  void total() {
    //  given
    ReviewSearchRequest request = new ReviewSearchRequest(user1Id, null, null, null,
        null, null, null, null);

    // when
    long result = reviewRepositoryImpl.getTotal(request);

    // then
    assertThat(result).isEqualTo(2);
  }


}
