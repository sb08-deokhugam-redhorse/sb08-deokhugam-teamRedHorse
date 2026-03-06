package com.redhorse.deokhugam.domain.review.service;

import com.redhorse.deokhugam.domain.book.entity.Book;
import com.redhorse.deokhugam.domain.book.repository.BookRepository;
import com.redhorse.deokhugam.domain.review.dto.ReviewCreateRequest;
import com.redhorse.deokhugam.domain.review.dto.ReviewDto;
import com.redhorse.deokhugam.domain.review.dto.ReviewLikeDto;
import com.redhorse.deokhugam.domain.review.dto.ReviewSearchRequest;
import com.redhorse.deokhugam.domain.review.dto.ReviewUpdateRequest;
import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.domain.review.entity.ReviewLike;
import com.redhorse.deokhugam.domain.review.mapper.ReviewMapper;
import com.redhorse.deokhugam.domain.review.repository.ReviewLikeRepository;
import com.redhorse.deokhugam.domain.review.repository.ReviewRepository;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

  private final ReviewRepository reviewRepository;
  private final ReviewLikeRepository reviewLikeRepository;
  private final UserRepository userRepository;
  private final BookRepository bookRepository;
  private final ReviewMapper reviewMapper;

  @Transactional
  @Override
  public ReviewDto create(ReviewCreateRequest request) {
    UUID bookId = request.bookId();
    UUID userId = request.userId();

    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new IllegalArgumentException("Book not exists"));
    User user = userRepository
        .findById(userId).orElseThrow(() -> new IllegalArgumentException("User not exists"));

    try {
      Review review = new Review(request.content(), request.rating(), book, user);
      reviewRepository.save(review);
      return reviewMapper.toDto(review);
    } catch (DataIntegrityViolationException e) {
      throw new IllegalStateException("bookId, userId exists");
    }

  }

  @Transactional
  @Override
  public ReviewDto update(UUID reviewId, UUID userId, ReviewUpdateRequest request) {
    String content = request.content();
    Integer rating = request.rating();

    if (content == null && rating == null) {
      throw new IllegalArgumentException("Both content and rating are null");
    }

    if (content != null && content.isBlank()) {
      throw new IllegalArgumentException("content cannot be empty");
    }

    Review review = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
        .orElseThrow(() -> new IllegalArgumentException("Review not exists"));

    if (!review.getUser().getId().equals(userId)) {
      throw new IllegalArgumentException("User did not write review");
    }

    review.update(content, rating);
    return reviewMapper.toDto(review);
  }

  @Transactional
  @Override
  public void softDelete(UUID reviewId, UUID userId) {
    Review review = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
        .orElseThrow(() -> new IllegalArgumentException("Review not exists"));

    if (!review.getUser().getId().equals(userId)) {
      throw new IllegalArgumentException("User did not write review");
    }

    review.delete();
  }

  @Transactional
  @Override
  public void hardDelete(UUID reviewId, UUID userId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new IllegalArgumentException("Review not exists"));

    if (!review.getUser().getId().equals(userId)) {
      throw new IllegalArgumentException("User did not write review");
    }

    reviewRepository.delete(review);
  }

  @Transactional
  @Override
  public ReviewLikeDto like(UUID reviewId, UUID userId) {
    Review review = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
        .orElseThrow(() -> new IllegalArgumentException("Review not exists"));

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not exists"));

    boolean like;

    Optional<ReviewLike> reviewLikeOptional = reviewLikeRepository.findByReviewIdAndUserId(reviewId,
        userId);

    // 리뷰 좋아요 테이블에 있는 경우
    if (reviewLikeOptional.isPresent()) {
      ReviewLike reviewLike = reviewLikeOptional.get();

      if (reviewLike.getDeletedAt() == null) {
        reviewLike.update(Instant.now());
        review.decrementLikeCount();
        like = false;
      } else {
        like = true;
        reviewLike.update(null);
        review.incrementLikeCount();
      }
    }
    // 리뷰 좋아요 테이블에 없는 경우
    else {
      ReviewLike newReviewLike = new ReviewLike(user, review);
      reviewLikeRepository.save(newReviewLike);
      review.incrementLikeCount();
      like = true;
    }
    ReviewLikeDto dto = new ReviewLikeDto(reviewId, userId, like);
    return dto;
  }

  @Transactional(readOnly = true)
  @Override
  public List<ReviewDto> findAll(ReviewSearchRequest request, UUID requestUserId) {
    int limit = request.limit() != null ? request.limit() : 50;
    String orderBy = request.orderBy() != null ? request.orderBy():  "createdAt";

    return reviewRepository.findAll().stream().map(reviewMapper::toDto).toList();
  }

}
