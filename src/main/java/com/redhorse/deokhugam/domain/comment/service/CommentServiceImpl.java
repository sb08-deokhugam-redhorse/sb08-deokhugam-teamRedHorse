package com.redhorse.deokhugam.domain.comment.service;

import com.redhorse.deokhugam.domain.comment.dto.CommentCreateRequest;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import com.redhorse.deokhugam.domain.comment.dto.CommentPageRequest;
import com.redhorse.deokhugam.domain.comment.dto.CommentUpdateRequest;
import com.redhorse.deokhugam.domain.comment.dto.CursorPageResponseCommentDto;
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
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommentServiceImpl implements CommentService {

  private final ReviewRepository reviewRepository;
  private final UserRepository userRepository;
  private final CommentRepository commentRepository;
  private final CommentMapper commentMapper;

  @Override
  public CommentDto create(CommentCreateRequest commentCreateRequest) {
    UUID reviewId = commentCreateRequest.reviewId();
    UUID userId = commentCreateRequest.userId();
    String content = commentCreateRequest.content();

    Review review = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
        .orElseThrow(() -> new ReviewNotFoundException(reviewId));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

    Comment comment = new Comment(content, review, user);

    Comment savedComment = commentRepository.save(comment);
    review.incrementCommentCount();

    log.info("[Comment-Service] 등록 작업 완료: commentId={}", savedComment.getId());

    return commentMapper.toDto(savedComment);
  }

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

  @Override
  @Transactional(readOnly = true)
  public CommentDto find(UUID commentId) {
    Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
        .orElseThrow(() -> new CommentNotFoundException(commentId));

    log.debug("[Comment-Service] 단건 조회 작업 완료: commentId={}", commentId);

    log.debug("[Comment-Service] 단건 조회 작업 완료: commentId={}", commentId);

    return commentMapper.toDto(comment);
  }

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
    log.info("[Comment-Service] 논리 삭제 작업 완료: commentId={}", commentId);
  }

  @Override
  public void hardDelete(UUID commentId, UUID requestUserId) {
    validateRequestUser(requestUserId);

    // 물리 삭제인 경우 논리 삭제된 댓글까지 지울 수 있어야 함.
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentNotFoundException(commentId));

    if (!comment.getUser().getId().equals(requestUserId)) {
      throw new CommentDeleteNotAllowedException(commentId);
    }

    commentRepository.delete(comment);

    log.info("[Comment-Service] 물리 삭제 작업 완료: commentId={}", commentId);
  }

  @Override
  @Transactional(readOnly = true)
  public CursorPageResponseCommentDto findAll(CommentPageRequest commentPageRequest) {
    Review review = reviewRepository.findByIdAndDeletedAtIsNull(commentPageRequest.reviewId())
        .orElseThrow(() -> new ReviewNotFoundException(commentPageRequest.reviewId()));

    int limit = commentPageRequest.limit() != null && commentPageRequest.limit() > 0
        ? commentPageRequest.limit() : 50;

    boolean isAsc = "ASC".equalsIgnoreCase(commentPageRequest.direction());

    UUID cursorId =
        (commentPageRequest.cursor() != null) ? UUID.fromString(commentPageRequest.cursor()) : null;

    Pageable pageable = PageRequest.of(0, limit + 1);

    List<Comment> comments;
    if (isAsc) {
      comments = commentRepository.findAllByCursorAsc(
          review.getId(),
          cursorId,
          commentPageRequest.after(),
          pageable);
    } else {
      comments = commentRepository.findAllByCursorDesc(
          review.getId(),
          cursorId,
          commentPageRequest.after(),
          pageable);
    }

    boolean hasNext = comments.size() > limit;
    List<Comment> content = hasNext ? comments.subList(0, limit) : comments;

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
        limit,
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
