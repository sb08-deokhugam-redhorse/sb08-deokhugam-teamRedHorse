package com.redhorse.deokhugam.domain.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import com.redhorse.deokhugam.domain.comment.dto.CommentCreateRequest;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import com.redhorse.deokhugam.domain.comment.entity.Comment;
import com.redhorse.deokhugam.domain.comment.mapper.CommentMapper;
import com.redhorse.deokhugam.domain.comment.repository.CommentRepository;
import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.domain.review.repository.ReviewRepository;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private CommentMapper commentMapper;

  @InjectMocks
  private CommentServiceImpl commentService;


  @Nested
  @DisplayName("댓글 등록 관련 테스트")
  class createCommentTests {

    @Test
    @DisplayName("댓글 등록 성공인 경우")
    void create_ShouldReturnCommentDto() {

      // given
      UUID reviewId = UUID.randomUUID();
      UUID userId = UUID.randomUUID();
      UUID commentId = UUID.randomUUID();
      Instant now = Instant.now();
      CommentCreateRequest commentReq = new CommentCreateRequest(reviewId, userId, "하이");

      Review mockReview = mock(Review.class);
      User mockUser = mock(User.class);

      given(reviewRepository.findById(eq(reviewId))).willReturn(Optional.of(mockReview));
      given(userRepository.findById(eq(userId))).willReturn(Optional.of(mockUser));
      given(mockUser.getNickname()).willReturn("감자");
      given(commentRepository.save(any(Comment.class)))
          .willAnswer(invocation -> invocation.getArgument(0));

      CommentDto commentDto = new CommentDto(commentId, reviewId, userId, mockUser.getNickname(), "하이",
          now, now);
      given(commentMapper.toDto(any(Comment.class))).willReturn(commentDto);

      // when
      CommentDto result = commentService.create(commentReq);

      // then
      assertThat(result).isEqualTo(commentDto);

      then(commentRepository).should().save(any(Comment.class));
      then(commentMapper).should().toDto(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 등록 실패 - 리뷰가 유효하지 않을 경우")
    void create_WhenNotFoundReview_ShouldThrowException() {
      UUID invalidReviewId = UUID.randomUUID();
      UUID userId = UUID.randomUUID();
      CommentCreateRequest commentReq = new CommentCreateRequest(invalidReviewId, userId, "하이");

      given(reviewRepository.findById(eq(invalidReviewId))).willReturn(Optional.empty());

      assertThatThrownBy(() -> commentService.create(commentReq))
          .isInstanceOf(IllegalArgumentException.class);

      // then
      then(commentRepository).should(never()).save(any(Comment.class));
      then(userRepository).should(never()).findById(eq(userId));
    }

    @Test
    @DisplayName("댓글 등록 실패 - 사용자가 유효하지 않을 경우")
    void create_WhenNotFoundUser_ShouldThrowException() {
      UUID reviewId = UUID.randomUUID();
      UUID invalidUserId = UUID.randomUUID();
      CommentCreateRequest commentReq = new CommentCreateRequest(reviewId, invalidUserId, "하이");

      Review mockReview = mock(Review.class);
      given(reviewRepository.findById(eq(reviewId))).willReturn(Optional.of(mockReview));
      given(userRepository.findById(eq(invalidUserId))).willReturn(Optional.empty());

      assertThatThrownBy(() -> commentService.create(commentReq))
          .isInstanceOf(IllegalArgumentException.class);

      // then
      then(commentRepository).should(never()).save(any(Comment.class));
    }
  }
}
