package com.redhorse.deokhugam.domain.comment.service;

import com.redhorse.deokhugam.domain.comment.dto.CommentCreateRequest;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import com.redhorse.deokhugam.domain.comment.entity.Comment;
import com.redhorse.deokhugam.domain.comment.mapper.CommentMapper;
import com.redhorse.deokhugam.domain.comment.repository.CommentRepository;
import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.domain.review.repository.ReviewRepository;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
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
    commentRepository.save(comment);

    return commentMapper.toDto(comment);
  }
}
