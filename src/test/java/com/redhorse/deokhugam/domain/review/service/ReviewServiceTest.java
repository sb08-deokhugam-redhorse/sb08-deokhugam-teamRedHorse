package com.redhorse.deokhugam.domain.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.redhorse.deokhugam.domain.book.entity.Book;
import com.redhorse.deokhugam.domain.book.repository.BookRepository;
import com.redhorse.deokhugam.domain.review.dto.ReviewCreateRequest;
import com.redhorse.deokhugam.domain.review.dto.ReviewDto;
import com.redhorse.deokhugam.domain.review.dto.ReviewUpdateRequest;
import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.domain.review.mapper.ReviewMapper;
import com.redhorse.deokhugam.domain.review.repository.ReviewRepository;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private BookRepository bookRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ReviewMapper reviewMapper;

  @InjectMocks
  private ReviewServiceImpl reviewService;

  private UUID reviewId;
  private UUID bookId;
  private UUID userId;
  private String content;
  private Integer rating;
  private Review review;
  private ReviewDto reviewDto;
  private Book book;
  private User user;
  private Instant date;

  @BeforeEach
  void setUp() {
    reviewId = UUID.randomUUID();
    bookId = UUID.randomUUID();
    userId = UUID.randomUUID();
    content = "content";
    rating = 5;
    date = Instant.now();

    book = new Book(
        "title",
        "author",
        "description",
        "publisher",
        LocalDate.now(),
        "isbn",
        "thumbnailUrl",
        Boolean.FALSE,
        Double.valueOf(0),
        Long.valueOf(0),
        new ArrayList<>());
    ReflectionTestUtils.setField(book, "id", bookId);

    user = new User("email", "nickname", "password");
    ReflectionTestUtils.setField(user, "id", userId);

    review = new Review(content, rating, book, user);
    ReflectionTestUtils.setField(review, "id", reviewId);
    reviewDto = new ReviewDto(
        reviewId,
        bookId,
        "bookTitle",
        "bookThumbnailUrl",
        userId,
        "userNickname",
        content,
        rating,
        0,
        0,
        false,
        date,
        date
    );
  }

  @Test
  @DisplayName("리뷰 생성 성공")
  void createReview_Success() {
    // given
    ReviewCreateRequest request = new ReviewCreateRequest(
        bookId, userId, content, rating
    );
    given(bookRepository.findById(eq(bookId))).willReturn(Optional.of(book));
    given(userRepository.findById(eq(userId))).willReturn(Optional.of(user));
    given(reviewRepository.save(any(Review.class))).willReturn(review);
    given(reviewMapper.toDto(any(Review.class))).willReturn(reviewDto);

    // when
    ReviewDto result = reviewService.create(request);

    // then
    assertThat(result).isEqualTo(reviewDto);
    verify(reviewRepository).save(any(Review.class));

  }

  @Test
  @DisplayName("리뷰 생성 실패 - 존재하지 않는 책일 경우")
  void createReview_Failure() {
    // given
    ReviewCreateRequest request = new ReviewCreateRequest(
        bookId, userId, content, rating
    );
    given(bookRepository.findById(eq(bookId))).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> reviewService.create(request))
        .isInstanceOf(IllegalArgumentException.class);

  }

  @Test
  @DisplayName("리뷰 수정 성공")
  void updateReview_Success() {
    // given
    ReviewUpdateRequest request = new ReviewUpdateRequest(
        "update", rating
    );

    given(reviewRepository.findById(eq(reviewId))).willReturn(Optional.of(review));

    ReviewDto updateReviewDto = new ReviewDto(
        reviewId,
        bookId,
        "bookTitle",
        "bookThumbnailUrl",
        userId,
        "userNickname",
        "update",
        rating,
        0,
        0,
        false,
        date,
        date
    );
    given(reviewMapper.toDto(any(Review.class))).willReturn(updateReviewDto);

    // when
    ReviewDto result = reviewService.update(reviewId, userId, request);

    // then
    assertThat(result).isEqualTo(updateReviewDto);
    assertThat(result.content()).isEqualTo("update");
  }

  @Test
  @DisplayName("리뷰 수정 실패 - 리뷰 작성자와 유저 아이디가 다를 경우")
  void updateReview_Failure() {
    // given
    ReviewUpdateRequest request = new ReviewUpdateRequest(
        "update", 3
    );

    UUID otherUserId = UUID.randomUUID();
    given(reviewRepository.findById(eq(reviewId))).willReturn(Optional.of(review));

    // when & then
    assertThatThrownBy(() -> reviewService.update(reviewId, otherUserId, request))
        .isInstanceOf(IllegalArgumentException.class);
  }

}
