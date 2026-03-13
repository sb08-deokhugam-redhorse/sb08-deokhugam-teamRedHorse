package com.redhorse.deokhugam.domain.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.redhorse.deokhugam.domain.book.entity.Book;
import com.redhorse.deokhugam.domain.book.repository.BookRepository;
import com.redhorse.deokhugam.domain.review.dto.CursorPageResponseReviewDto;
import com.redhorse.deokhugam.domain.review.dto.ReviewCreateRequest;
import com.redhorse.deokhugam.domain.review.dto.ReviewDto;
import com.redhorse.deokhugam.domain.review.dto.ReviewSearchRequest;
import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.domain.review.entity.ReviewLike;
import com.redhorse.deokhugam.domain.review.exception.BookIdUserIdExistsException;
import com.redhorse.deokhugam.domain.review.repository.ReviewLikeRepository;
import com.redhorse.deokhugam.domain.review.repository.ReviewRepository;
import com.redhorse.deokhugam.domain.review.service.ReviewService;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.exception.UserNotFoundException;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ReviewIntegrationTest {

  @Autowired
  private ReviewRepository reviewRepository;

  @Autowired
  private ReviewLikeRepository reviewLikeRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private ReviewService reviewService;

  private UUID userId;
  private UUID bookId;
  private Book book;
  private User user;

  @BeforeEach
  void setUp() {
    user = userRepository.save(new User("a@test.com", "user1", "pw"));
    book = bookRepository.save(
        new Book("title", "author", "description", "publisher", LocalDate.now(),
            "isbn1", "thumbnailUrl", false, 3.0, 3L, new ArrayList<>()));

    userId = user.getId();
    bookId = book.getId();
  }

  @Nested
  @DisplayName("리뷰 등록 테스트")
  class create {

    private ReviewCreateRequest request;

    @BeforeEach
    void setUp() {
      request = new ReviewCreateRequest(bookId, userId, "content", 5);
    }

    @Test
    @DisplayName("성공")
    void create_Success() {
      // when
      ReviewDto result = reviewService.create(request);

      // then
      assertThat(reviewRepository.count()).isEqualTo(1);
      assertThat(result.content()).isEqualTo("content");
      assertThat(result.rating()).isEqualTo(5);
    }

    @Test
    @DisplayName("실패 - 중복 리뷰 등록 시 실패")
    void create_Failure() {
      // given
      reviewService.create(request);

      // when & then
      assertThatThrownBy(() -> {
        reviewService.create(request);
      }).isInstanceOf(BookIdUserIdExistsException.class);
    }
  }

  @Nested
  @DisplayName("리뷰 목록 조회 테스트")
  class findAll {

    private ReviewSearchRequest request;

    @BeforeEach
    void setUp() {
      User user1 = userRepository.save(new User("b@test.com", "user2", "pw"));
      User user2 = userRepository.save(new User("c@test.com", "user3", "pw"));

      Book book1 = bookRepository.save(
          new Book("test", "user", "hello", "publisher", LocalDate.now(),
              "isbn2", "thumbnailUrl", false, 4.0, 9L, new ArrayList<>()));

      reviewRepository.saveAll(List.of(
          new Review("test1", 1, book1, user1),
          new Review("test2", 2, book1, user2),
          new Review("test3", 3, book1, user),
          new Review("test5", 1, book, user1),
          new Review("test2", 4, book, user2)
      ));

      Review review = reviewRepository.save(new Review("test4", 2, book, user));

      reviewLikeRepository.save(new ReviewLike(user, review));

      request = new ReviewSearchRequest(null, null, null, null,
          null, null, null, 3);
    }

    @Test
    @DisplayName("성공")
    void findAll_Success() {
      // when
      CursorPageResponseReviewDto result = reviewService.findAll(request, userId);

      // then
      assertThat(result.content()).hasSize(3);
      assertThat(result.content()).filteredOn(ReviewDto::likedByMe).hasSize(1);
      assertThat(result.hasNext()).isTrue();
      assertThat(result.nextCursor()).isNotNull();
      assertThat(result.totalElements()).isEqualTo(6);
    }

    @Test
    @DisplayName("실패 - 유저가 존재하지 않을 경우")
    void findAll_Failure() {
      // given
      UUID otherId = UUID.randomUUID();

      // when & then
      assertThatThrownBy(() ->
          reviewService.findAll(request, otherId))
          .isInstanceOf(UserNotFoundException.class);
    }
  }

}
