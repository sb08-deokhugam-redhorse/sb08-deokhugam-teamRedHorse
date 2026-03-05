package com.redhorse.deokhugam.domain.review.service;

import com.redhorse.deokhugam.domain.book.entity.Book;
import com.redhorse.deokhugam.domain.book.repository.BookRepository;
import com.redhorse.deokhugam.domain.comment.repository.CommentRepository;
import com.redhorse.deokhugam.domain.review.dto.ReviewCreateRequest;
import com.redhorse.deokhugam.domain.review.dto.ReviewDto;
import com.redhorse.deokhugam.domain.review.dto.ReviewUpdateRequest;
import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.domain.review.mapper.ReviewMapper;
import com.redhorse.deokhugam.domain.review.repository.ReviewRepository;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

  private final ReviewRepository reviewRepository;
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

    Review review = reviewRepository.findById(reviewId)
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
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new IllegalArgumentException("Review not exists"));

    if(!review.getUser().getId().equals(userId)){
      throw new IllegalArgumentException("User did not write review");
    }

    review.delete();
  }

  @Transactional
  @Override
  public void hardDelete(UUID reviewId, UUID userId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new IllegalArgumentException("Review not exists"));

    if(!review.getUser().getId().equals(userId)){
      throw new IllegalArgumentException("User did not write review");
    }

    reviewRepository.delete(review);
  }

}
