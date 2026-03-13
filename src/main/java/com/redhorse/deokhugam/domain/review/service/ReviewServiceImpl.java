package com.redhorse.deokhugam.domain.review.service;

import com.redhorse.deokhugam.domain.book.entity.Book;
import com.redhorse.deokhugam.domain.book.exception.BookNotFoundException;
import com.redhorse.deokhugam.domain.book.repository.BookRepository;
import com.redhorse.deokhugam.domain.review.dto.CursorPageResponseReviewDto;
import com.redhorse.deokhugam.domain.review.dto.ReviewCreateRequest;
import com.redhorse.deokhugam.domain.review.dto.ReviewDto;
import com.redhorse.deokhugam.domain.review.dto.ReviewLikeDto;
import com.redhorse.deokhugam.domain.review.dto.ReviewSearchRequest;
import com.redhorse.deokhugam.domain.review.dto.ReviewUpdateRequest;
import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.domain.review.entity.ReviewLike;
import com.redhorse.deokhugam.domain.review.exception.BookIdUserIdExistsException;
import com.redhorse.deokhugam.domain.review.exception.OnlyTheReviewAuthorException;
import com.redhorse.deokhugam.domain.review.exception.ReviewNotFoundException;
import com.redhorse.deokhugam.domain.review.exception.ReviewValidationException;
import com.redhorse.deokhugam.domain.review.mapper.ReviewMapper;
import com.redhorse.deokhugam.domain.review.repository.ReviewLikeRepository;
import com.redhorse.deokhugam.domain.review.repository.ReviewRepository;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.exception.UserNotFoundException;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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

    Book book = getBook(bookId);
    User user = getUser(userId);

    if (reviewRepository.existsByBookIdAndUserId(bookId, userId)) {
      throw new BookIdUserIdExistsException(bookId, userId);
    }

    try {
      Review review = new Review(request.content(), request.rating(), book, user);
      reviewRepository.save(review);
      log.info("[Review-Service] 생성 작업 완료: reviewId = {}, userId = {}", review.getId(), userId);
      return reviewMapper.toDto(review);
    } catch (DataIntegrityViolationException e) {
      throw new BookIdUserIdExistsException(bookId, userId);
    }

  }

  @Caching(evict = {@CacheEvict(value = "review", key = "#reviewId")})
  @Transactional
  @Override
  public ReviewDto update(UUID reviewId, UUID userId, ReviewUpdateRequest request) {
    String content = request.content();
    Integer rating = request.rating();

    if (content == null && rating == null) {
      throw new ReviewValidationException("내용과 별점을 작성해야 합니다.");
    }

    if (content != null && content.isBlank()) {
      throw new ReviewValidationException("내용이 비면 안됩니다.");
    }

    Review review = getReviewWithLock(reviewId);
    validateReviewAuthor(review, userId);

    review.update(content, rating);
    log.info("[Review-Service] 수정 작업 완료: reviewId = {}, userId = {}", reviewId, userId);
    return reviewMapper.toDto(review);
  }

  @CacheEvict(value = "review", key = "#reviewId")
  @Transactional
  @Override
  public void softDelete(UUID reviewId, UUID userId) {
    Review review = getReviewWithLock(reviewId);
    validateReviewAuthor(review, userId);

    review.delete();
    log.info("[Review-Service] 논리 삭제 작업 완료: reviewId = {}", reviewId);
  }

  @CacheEvict(value = "review", key = "#reviewId")
  @Transactional
  @Override
  public void hardDelete(UUID reviewId, UUID userId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ReviewNotFoundException(reviewId));

    validateReviewAuthor(review, userId);

    reviewRepository.delete(review);
    log.info("[Review-Service] 물리 삭제 작업 완료: reviewId = {}", reviewId);
  }

  @CacheEvict(value = "review", key = "#reviewId")
  @Transactional
  @Override
  public ReviewLikeDto like(UUID reviewId, UUID userId) {
    Review review = getReviewWithLock(reviewId);
    User user = getUser(userId);

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
    log.info("[Review-Service] 좋아요 작업 완료: reviewId = {}", reviewId);
    return dto;
  }

  @Transactional(readOnly = true)
  @Override
  public CursorPageResponseReviewDto findAll(ReviewSearchRequest request, UUID requestUserId) {
    getUser(requestUserId);

    Slice<Review> slice = reviewRepository.getAllReviews(request);

    List<Review> reviews = slice.getContent();

    List<UUID> reviewIds = reviews.stream().map(Review::getId).toList();

    // 리뷰 좋아요 체크
    Set<UUID> likedReviewIds = reviewIds.isEmpty() ? Collections.emptySet()
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

    log.info("[Review-Service] 목록 조회 작업 완료");
    return dto;
  }

  private String getOrderBy(String orderBy, Review lastReview) {
    if ("rating".equals(orderBy)) {
      return String.valueOf(lastReview.getRating());
    }
    return lastReview.getCreatedAt().toString();
  }

  @Cacheable(value = "review", key = "#reviewId + '_' + #userId")
  @Transactional(readOnly = true)
  @Override
  public ReviewDto findById(UUID reviewId, UUID userId) {
    log.info("[Review-Service] DB 조회 실행 (캐시 미스 시에만 찍힘): reviewId = {}", reviewId);
    Review review = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
        .orElseThrow(() -> new ReviewNotFoundException(reviewId));
    getUser(userId);

    boolean likedByMe = reviewLikeRepository.findByReviewIdAndUserIdAndDeletedAtIsNull(reviewId,
            userId)
        .isPresent();

    log.info("[Review-Service] 상세 정보 조회 작업 완료: reviewId = {}", reviewId);
    return reviewMapper.toDto(review, likedByMe);
  }

  // 공통 로직
  private Book getBook(UUID bookId) {
    return bookRepository.findById(bookId)
        .orElseThrow(() -> new BookNotFoundException(bookId));
  }

  private User getUser(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
  }

  private Review getReviewWithLock(UUID reviewId) {
    return reviewRepository.findByIdForUpdate(reviewId)
        .orElseThrow(() -> new ReviewNotFoundException(reviewId));
  }

  private void validateReviewAuthor(Review review, UUID userId) {
    if (!review.getUser().getId().equals(userId)) {
      throw new OnlyTheReviewAuthorException(userId);
    }
  }


}
