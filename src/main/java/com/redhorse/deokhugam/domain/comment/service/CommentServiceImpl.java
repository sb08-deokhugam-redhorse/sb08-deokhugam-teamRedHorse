package com.redhorse.deokhugam.domain.comment.service;

import com.redhorse.deokhugam.domain.comment.dto.*;
import com.redhorse.deokhugam.domain.comment.entity.Comment;
import com.redhorse.deokhugam.domain.comment.exception.CommentDeleteNotAllowedException;
import com.redhorse.deokhugam.domain.comment.exception.CommentNotFoundException;
import com.redhorse.deokhugam.domain.comment.exception.CommentUpdateNotAllowedException;
import com.redhorse.deokhugam.domain.comment.mapper.CommentMapper;
import com.redhorse.deokhugam.domain.comment.repository.CommentRepository;
import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.domain.review.exception.ReviewNotFoundException;
import com.redhorse.deokhugam.domain.review.repository.ReviewRepository;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.exception.UserNotFoundException;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommentServiceImpl implements CommentService {

  private final ReviewRepository reviewRepository;
  private final UserRepository userRepository;
  private final CommentRepository commentRepository;
  private final CommentMapper commentMapper;
  private final CacheManager cacheManager;

  @Caching(evict = {
          @CacheEvict(value = "review", key = "#commentCreateRequest.reviewId()")
  })
  @Override
  public CommentDto create(CommentCreateRequest commentCreateRequest) {
    UUID reviewId = commentCreateRequest.reviewId();
    UUID userId = commentCreateRequest.userId();
    String content = commentCreateRequest.content();

    Review review = reviewRepository.findByIdForUpdate(reviewId)
        .orElseThrow(() -> new ReviewNotFoundException(reviewId));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

    Comment comment = new Comment(content, review, user);

    Comment savedComment = commentRepository.save(comment);
    review.incrementCommentCount();

    log.info("[Comment-Service] 등록 작업 완료: commentId={}", savedComment.getId());

    return commentMapper.toDto(savedComment);
  }

  @CacheEvict(value = "comment", key = "#commentId")
  @Override
  public CommentDto update(UUID commentId, UUID requestUserId,
      CommentUpdateRequest commentUpdateRequest) {
    validateRequestUser(requestUserId);

    Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
        .orElseThrow(() -> new CommentNotFoundException(commentId));

    if (!comment.getUser().getId().equals(requestUserId)) {
      throw new CommentUpdateNotAllowedException(commentId);
    }

    comment.update(commentUpdateRequest.content());

    log.info("[Comment-Service] 수정 작업 완료: commentId={}", comment.getId());

    return commentMapper.toDto(comment);
  }

  @Cacheable(value = "comment", key = "#commentId")
  @Override
  @Transactional(readOnly = true)
  public CommentDto find(UUID commentId) {
    Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
        .orElseThrow(() -> new CommentNotFoundException(commentId));

    log.debug("[Comment-Service] 단건 조회 작업 완료: commentId={}", commentId);

    return commentMapper.toDto(comment);
  }

  @Caching(evict = {
          @CacheEvict(value = "comment", key = "#commentId")
  })
  @Override
  public void softDelete(UUID commentId, UUID requestUserId) {
    validateRequestUser(requestUserId);

    Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
        .orElseThrow(() -> new CommentNotFoundException(commentId));

    if (!comment.getUser().getId().equals(requestUserId)) {
      throw new CommentDeleteNotAllowedException(commentId);
    }

    comment.softDelete();
    Review review = comment.getReview();
    review.decrementCommentCount();

    Optional.ofNullable(cacheManager.getCache("review"))
                    .ifPresent(cache -> cache.evict(review.getId()));

    log.info("[Comment-Service] 논리 삭제 작업 완료: commentId={}", commentId);
  }

  @Caching(evict = {
          @CacheEvict(value = "comment", key = "#commentId")
  })
  @Override
  public void hardDelete(UUID commentId, UUID requestUserId) {
    validateRequestUser(requestUserId);

    // 물리 삭제인 경우 논리 삭제된 댓글까지 지울 수 있어야 함.
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentNotFoundException(commentId));

    if (!comment.getUser().getId().equals(requestUserId)) {
      throw new CommentDeleteNotAllowedException(commentId);
    }

    // 논리 삭제가 이루어지지 않은 경우에만 count 깎기
    if (comment.getDeletedAt() == null) {
      Review review = comment.getReview();
      review.decrementCommentCount();

      Optional.ofNullable(cacheManager.getCache("review"))
          .ifPresent(cache -> cache.evict(comment.getReview().getId()));
    }

    commentRepository.delete(comment);

    log.info("[Comment-Service] 물리 삭제 작업 완료: commentId={}", commentId);
  }

  @Override
  @Transactional(readOnly = true)
  public CursorPageResponseCommentDto findAll(CommentPageRequest commentPageRequest) {
    if (!reviewRepository.existsByIdAndDeletedAtIsNull(commentPageRequest.reviewId())) {
      throw new ReviewNotFoundException(commentPageRequest.reviewId());
    }

    List<Comment> comments = commentRepository.findAllByCursor(commentPageRequest);

    boolean hasNext = comments.size() > commentPageRequest.limit();
    List<Comment> content = hasNext ? comments.subList(0, commentPageRequest.limit()) : comments;

    String nextCursor = null;
    Instant nextAfter = null;

    if (!content.isEmpty() && hasNext) {
      Comment lastComment = content.get(content.size() - 1);
      nextCursor = lastComment.getId().toString();
      nextAfter = lastComment.getCreatedAt();
    }

    long totalElements = commentRepository.countByReviewIdAndDeletedAtIsNull(
        commentPageRequest.reviewId());

    log.debug("[Comment-Service] 다건 조회 작업 완료: 결과 건수={}, 전체 건수={}, 다음 커서 존재={}",
        content.size(), totalElements, hasNext);

    return new CursorPageResponseCommentDto(
        content.stream().map(commentMapper::toDto).toList(),
        nextCursor,
        nextAfter,
        commentPageRequest.limit(),
        totalElements,
        hasNext
    );
  }

  private void validateRequestUser(UUID requestUserId) {
    if (!userRepository.existsById(requestUserId)) {
      throw new UserNotFoundException(requestUserId);
    }
  }
}
