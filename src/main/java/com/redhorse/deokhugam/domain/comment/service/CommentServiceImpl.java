package com.redhorse.deokhugam.domain.comment.service;

import com.redhorse.deokhugam.domain.comment.dto.CommentCreateRequest;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import com.redhorse.deokhugam.domain.comment.dto.CommentUpdateRequest;
import com.redhorse.deokhugam.domain.comment.entity.Comment;
import com.redhorse.deokhugam.domain.comment.mapper.CommentMapper;
import com.redhorse.deokhugam.domain.comment.repository.CommentRepository;
import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.domain.review.repository.ReviewRepository;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new IllegalArgumentException("Review Not Found"));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User Not Found"));

    Comment comment = new Comment(content, review, user);

    Comment savedComment = commentRepository.save(comment);

    return commentMapper.toDto(savedComment);
  }

  @Override
  public CommentDto update(UUID commentId, UUID requestUserId,
      CommentUpdateRequest commentUpdateRequest) {
    Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
        .orElseThrow(() -> new IllegalArgumentException("Comment Not Found"));

    if (!comment.getUser().getId().equals(requestUserId)) {
      throw new IllegalArgumentException("자신이 작성한 댓글만 수정할 수 있습니다.");
    }

    comment.update(commentUpdateRequest.content());

    return commentMapper.toDto(comment);
  }

  @Override
  @Transactional(readOnly = true)
  public CommentDto find(UUID commentId) {
    Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
        .orElseThrow(() -> new IllegalArgumentException("Comment Not Found"));

    return commentMapper.toDto(comment);
  }

  @Override
  public void softDelete(UUID commentId, UUID requestUserId) {
    Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
        .orElseThrow(() -> new IllegalArgumentException("Comment Not Found"));

    if (!comment.getUser().getId().equals(requestUserId)) {
      throw new IllegalArgumentException("자신이 작성한 댓글만 삭제할 수 있습니다.");
    }

    comment.softDelete();
  }
}
