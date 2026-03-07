package com.redhorse.deokhugam.domain.review.service;

import com.redhorse.deokhugam.domain.book.entity.Book;
import com.redhorse.deokhugam.domain.book.repository.BookRepository;
import com.redhorse.deokhugam.domain.review.dto.CursorPageResponseReviewDto;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Slice;
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

    Review review = reviewRepository.findByIdForUpdate(reviewId)
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
    Review review = reviewRepository.findByIdForUpdate(reviewId)
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
    Review review = reviewRepository.findByIdForUpdate(reviewId)
        .orElseThrow(() -> new IllegalArgumentException("Review not exists"));

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not exists"));

    boolean like;

    Optional<ReviewLike> reviewLikeOptional = reviewLikeRepository.findByIdForUpdate(reviewId,
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
  public CursorPageResponseReviewDto findAll(ReviewSearchRequest request, UUID requestUserId) {
    if (!userRepository.existsById(requestUserId)) {
      throw new IllegalArgumentException("User not exists");
    }

    Slice<Review> slice = reviewRepository.getAllReviews(request);

    List<Review> reviews = slice.getContent();

    List<UUID> reviewIds = reviews.stream().map(Review::getId).toList();

    // 리뷰 좋아요 체크
    Set<UUID> likedReviewIds = reviewIds.isEmpty() ?  Collections.emptySet()
        : reviewLikeRepository.findAllByUserIdAndReviewIdInAndDeletedAtIsNull(
            requestUserId, reviewIds)
        .stream()
        .map(like -> like.getReview().getId())
        .collect(Collectors.toSet());

    // 리뷰 좋아요 합치기
    List<ReviewDto> content = reviews
        .stream()
        .map(review -> reviewMapper.toDto(review, likedReviewIds.contains(review.getId())))
        .toList();

    String nextCursor = null;
    Instant nextAfter = null;

    if (slice.hasNext() && !reviews.isEmpty()) {
      Review lastReview = reviews.get(reviews.size() - 1);
      nextCursor = getOrderBy(request.orderBy(), lastReview);
      nextAfter = lastReview.getCreatedAt();
    }

    long totalElements = reviewRepository.getTotal(request);

    CursorPageResponseReviewDto dto = new CursorPageResponseReviewDto(
        content, nextCursor, nextAfter, content.size(), totalElements, slice.hasNext()
    );

    return dto;
  }

  private String getOrderBy(String orderBy, Review lastReview) {
    if ("rating".equals(orderBy)) {
      return String.valueOf(lastReview.getRating());
    }
    return lastReview.getCreatedAt().toString();
  }

  @Transactional(readOnly = true)
  @Override
  public ReviewDto findById(UUID reviewId, UUID userId) {
    Review review = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
        .orElseThrow(() -> new IllegalArgumentException("Review not exists"));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not exists"));

    boolean likedByMe = reviewLikeRepository.findByReviewIdAndUserIdAndDeletedAtIsNull(reviewId,
            userId)
        .isPresent();
    return reviewMapper.toDto(review, likedByMe);
  }

}
