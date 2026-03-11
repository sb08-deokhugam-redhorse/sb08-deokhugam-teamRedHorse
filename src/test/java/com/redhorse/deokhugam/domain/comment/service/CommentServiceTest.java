package com.redhorse.deokhugam.domain.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.redhorse.deokhugam.domain.comment.dto.CommentCreateRequest;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import com.redhorse.deokhugam.domain.comment.dto.CommentPageRequest;
import com.redhorse.deokhugam.domain.comment.dto.CommentUpdateRequest;
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
import java.util.ArrayList;
import java.util.List;
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

      given(reviewRepository.findByIdAndDeletedAtIsNull(eq(reviewId))).willReturn(
          Optional.of(mockReview));
      given(userRepository.findById(eq(userId))).willReturn(Optional.of(mockUser));
      given(mockUser.getNickname()).willReturn("감자");
      given(commentRepository.save(any(Comment.class)))
          .willAnswer(invocation -> invocation.getArgument(0));

      CommentDto commentDto = new CommentDto(commentId, reviewId, userId, mockUser.getNickname(),
          "하이", now, now);
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
      // given
      UUID invalidReviewId = UUID.randomUUID();
      UUID userId = UUID.randomUUID();
      CommentCreateRequest commentReq = new CommentCreateRequest(invalidReviewId, userId, "하이");

      given(reviewRepository.findByIdAndDeletedAtIsNull(eq(invalidReviewId))).willReturn(
          Optional.empty());

      // when & then
      assertThatThrownBy(() -> commentService.create(commentReq))
          .isInstanceOf(ReviewNotFoundException.class);

      then(commentRepository).should(never()).save(any(Comment.class));
      then(userRepository).should(never()).findById(eq(userId));
    }

    @Test
    @DisplayName("댓글 등록 실패 - 사용자가 유효하지 않을 경우")
    void create_WhenNotFoundUser_ShouldThrowException() {
      // given
      UUID reviewId = UUID.randomUUID();
      UUID invalidUserId = UUID.randomUUID();
      CommentCreateRequest commentReq = new CommentCreateRequest(reviewId, invalidUserId, "하이");

      Review mockReview = mock(Review.class);
      given(reviewRepository.findByIdAndDeletedAtIsNull(eq(reviewId))).willReturn(
          Optional.of(mockReview));
      given(userRepository.findById(eq(invalidUserId))).willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> commentService.create(commentReq))
          .isInstanceOf(UserNotFoundException.class);

      // then
      then(commentRepository).should(never()).save(any(Comment.class));
    }
  }

  @Nested
  @DisplayName("댓글 수정 관련 테스트")
  class updateCommentTests {

    @Test
    @DisplayName("댓글 수정 성공")
    void update_ShouldReturnCommentDto() {
      // given
      UUID commentId = UUID.randomUUID();
      UUID requestUserId = UUID.randomUUID();
      CommentUpdateRequest commentReq = new CommentUpdateRequest("댓글 수정 테스트");

      given(userRepository.existsById(requestUserId)).willReturn(true);

      User mockUser = mock(User.class);
      given(mockUser.getId()).willReturn(requestUserId);

      Comment mockComment = mock(Comment.class);
      given(mockComment.getUser()).willReturn(mockUser);
      given(commentRepository.findByIdAndDeletedAtIsNull(eq(commentId))).willReturn(
          Optional.of(mockComment));

      CommentDto expectedDto = new CommentDto(commentId, UUID.randomUUID(), requestUserId, "감자",
          "댓글 수정 테스트", Instant.now(), Instant.now());
      given(commentMapper.toDto(mockComment)).willReturn(expectedDto);

      // when
      CommentDto result = commentService.update(commentId, requestUserId, commentReq);

      // then
      assertThat(result.content()).isEqualTo("댓글 수정 테스트");
    }

    @Test
    @DisplayName("댓글 수정 실패 - 댓글 작성자가 아닐 경우")
    void update_WhenAuthorIsDifferent_ShouldThrowException() {
      // given
      UUID commentId = UUID.randomUUID();
      UUID requestUserId = UUID.randomUUID();
      UUID authorId = UUID.randomUUID();
      CommentUpdateRequest commentReq = new CommentUpdateRequest("댓글 수정 테스트");

      given(userRepository.existsById(requestUserId)).willReturn(true);

      User mockAuthor = mock(User.class);
      given(mockAuthor.getId()).willReturn(authorId);

      Comment mockComment = mock(Comment.class);
      given(mockComment.getUser()).willReturn(mockAuthor);
      given(commentRepository.findByIdAndDeletedAtIsNull(eq(commentId))).willReturn(
          Optional.of(mockComment));

      // when & then
      assertThatThrownBy(() -> commentService.update(commentId, requestUserId, commentReq))
          .isInstanceOf(CommentUpdateNotAllowedException.class);

      verify(mockComment, never()).update(anyString());
    }

    @Test
    @DisplayName("댓글 수정 실패 - 요청한 유저가 존재하지 않을 경우")
    void update_WhenUserNotFound_ShouldThrowException() {
      // given
      UUID requestUserId = UUID.randomUUID();
      given(userRepository.existsById(requestUserId)).willReturn(false);

      // when & then
      assertThatThrownBy(() -> commentService.update(UUID.randomUUID(), requestUserId,
          mock(CommentUpdateRequest.class)))
          .isInstanceOf(UserNotFoundException.class);
    }
  }

  @Nested
  @DisplayName("댓글 단건 조회 관련 테스트")
  class findCommentTests {

    @Test
    @DisplayName("댓글 단건 조회 성공")
    void find_ShouldReturnCommentDto() {
      // given
      UUID commentId = UUID.randomUUID();
      Comment mockComment = mock(Comment.class);
      CommentDto expectedDto = new CommentDto(commentId, UUID.randomUUID(), UUID.randomUUID(),
          "감자",
          "댓글 수정 테스트", Instant.now(), Instant.now());

      given(commentRepository.findByIdAndDeletedAtIsNull(eq(commentId))).willReturn(
          Optional.of(mockComment));
      given(commentMapper.toDto(mockComment)).willReturn(expectedDto);

      // when
      CommentDto result = commentService.find(commentId);

      // then
      assertThat(result).isNotNull();
      assertThat(result).isEqualTo(expectedDto);

      then(commentRepository).should().findByIdAndDeletedAtIsNull(eq(commentId));
      then(commentMapper).should().toDto(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 단건 조회 실패 - 댓글이 유효하지 않을 경우")
    void find_WhenNotFoundComment_ShouldThrowException() {
      // given
      UUID invalidCommentId = UUID.randomUUID();

      given(commentRepository.findByIdAndDeletedAtIsNull(eq(invalidCommentId))).willReturn(
          Optional.empty());

      // when & then
      assertThatThrownBy(() -> commentService.find(invalidCommentId))
          .isInstanceOf(CommentNotFoundException.class);
    }
  }

  @Nested
  @DisplayName("댓글 논리 삭제 관련 테스트")
  class softDeleteCommentTests {

    @Test
    @DisplayName("댓글 논리 삭제 성공")
    void softDelete_Success() {
      // given
      UUID commentId = UUID.randomUUID();
      UUID requestUserId = UUID.randomUUID();

      given(userRepository.existsById(requestUserId)).willReturn(true);

      User mockUser = mock(User.class);
      given(mockUser.getId()).willReturn(requestUserId);

      Review mockReview = mock(Review.class);

      Comment mockComment = mock(Comment.class);
      given(mockComment.getUser()).willReturn(mockUser);
      given(mockComment.getReview()).willReturn(mockReview);
      given(commentRepository.findByIdAndDeletedAtIsNull(eq(commentId))).willReturn(
          Optional.of(mockComment));

      // when
      commentService.softDelete(commentId, requestUserId);

      // then
      then(commentRepository).should().findByIdAndDeletedAtIsNull(eq(commentId));
      then(mockComment).should().softDelete();
    }

    @Test
    @DisplayName("댓글 논리 삭제 실패 - 댓글 작성자가 아닐 경우")
    void softDelete_WhenAuthorIsDifferent_ShouldThrowException() {
      // given
      UUID commentId = UUID.randomUUID();
      UUID requestUserId = UUID.randomUUID();
      UUID authorId = UUID.randomUUID();

      given(userRepository.existsById(requestUserId)).willReturn(true);

      User mockAuthor = mock(User.class);
      given(mockAuthor.getId()).willReturn(authorId);

      Comment mockComment = mock(Comment.class);
      given(mockComment.getUser()).willReturn(mockAuthor);
      given(commentRepository.findByIdAndDeletedAtIsNull(eq(commentId))).willReturn(
          Optional.of(mockComment));

      // when & then
      assertThatThrownBy(() -> commentService.softDelete(commentId, requestUserId))
          .isInstanceOf(CommentDeleteNotAllowedException.class);

      then(mockComment).should(never()).softDelete();
    }

    @Test
    @DisplayName("댓글 논리 삭제 실패 - 유효하지 않은 댓글일 경우")
    void softDelete_WhenNotFoundComment_ShouldThrowException() {
      // given
      UUID invalidCommentId = UUID.randomUUID();
      UUID requestUserId = UUID.randomUUID();

      given(userRepository.existsById(requestUserId)).willReturn(true);
      given(commentRepository.findByIdAndDeletedAtIsNull(eq(invalidCommentId))).willReturn(
          Optional.empty());

      // when & then
      assertThatThrownBy(() -> commentService.softDelete(invalidCommentId, requestUserId))
          .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    @DisplayName("댓글 논리 삭제 실패 - 요청한 유저가 존재하지 않을 경우")
    void softDelete_WhenUserNotFound_ShouldThrowException() {
      // given
      UUID commentId = UUID.randomUUID();
      UUID requestUserId = UUID.randomUUID();
      given(userRepository.existsById(requestUserId)).willReturn(false);

      // when & then
      assertThatThrownBy(() -> commentService.softDelete(commentId, requestUserId))
          .isInstanceOf(UserNotFoundException.class);

      then(commentRepository).should(never()).findByIdAndDeletedAtIsNull(any());
    }
  }

  @Nested
  @DisplayName("댓글 물리 삭제 관련 테스트")
  class hardDeleteCommentTests {

    @Test
    @DisplayName("댓글 물리 삭제 성공")
    void hardDelete_Success() {
      // given
      UUID commentId = UUID.randomUUID();
      UUID requestUserId = UUID.randomUUID();

      given(userRepository.existsById(requestUserId)).willReturn(true);

      User mockUser = mock(User.class);
      given(mockUser.getId()).willReturn(requestUserId);

      Comment mockComment = mock(Comment.class);
      given(mockComment.getUser()).willReturn(mockUser);
      given(commentRepository.findById(eq(commentId))).willReturn(Optional.of(mockComment));

      // when
      commentService.hardDelete(commentId, requestUserId);

      // then
      then(commentRepository).should().findById(eq(commentId));
      then(commentRepository).should().delete(eq(mockComment));
    }

    @Test
    @DisplayName("댓글 물리 삭제 실패 - 댓글 작성자가 아닐 경우")
    void hardDelete_WhenAuthorIsDifferent_ShouldThrowException() {
      // given
      UUID commentId = UUID.randomUUID();
      UUID requestUserId = UUID.randomUUID();
      UUID authorId = UUID.randomUUID();

      given(userRepository.existsById(requestUserId)).willReturn(true);

      User mockAuthor = mock(User.class);
      given(mockAuthor.getId()).willReturn(authorId);

      Comment mockComment = mock(Comment.class);
      given(mockComment.getUser()).willReturn(mockAuthor);
      given(commentRepository.findById(eq(commentId))).willReturn(Optional.of(mockComment));

      // when & then
      assertThatThrownBy(() -> commentService.hardDelete(commentId, requestUserId))
          .isInstanceOf(CommentDeleteNotAllowedException.class);

      then(commentRepository).should(never()).delete(mockComment);
    }

    @Test
    @DisplayName("댓글 물리 삭제 실패 - 유효하지 않은 댓글일 경우")
    void hardDelete_WhenNotFoundComment_ShouldThrowException() {
      // given
      UUID invalidCommentId = UUID.randomUUID();
      UUID requestUserId = UUID.randomUUID();

      given(userRepository.existsById(requestUserId)).willReturn(true);
      given(commentRepository.findById(eq(invalidCommentId))).willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> commentService.hardDelete(invalidCommentId, requestUserId))
          .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    @DisplayName("댓글 물리 삭제 실패 - 요청한 유저가 존재하지 않을 경우")
    void hardDelete_WhenUserNotFound_ShouldThrowException() {
      // given
      UUID commentId = UUID.randomUUID();
      UUID requestUserId = UUID.randomUUID();
      given(userRepository.existsById(requestUserId)).willReturn(false);

      // when & then
      assertThatThrownBy(() -> commentService.hardDelete(commentId, requestUserId))
          .isInstanceOf(UserNotFoundException.class);

      then(commentRepository).should(never()).findById(any());
    }
  }

  @Nested
  @DisplayName("댓글 목록 조회 관련 테스트")
  class findAllCommentTests {

    @Test
    @DisplayName("댓글 목록 조회 성공 - 다음 페이지가 존재하는 경우")
    void findAll_WhenHasNextPage_ShouldReturnResponse() {
      // given
      UUID reviewId = UUID.randomUUID();
      int limit = 5;
      CommentPageRequest request = new CommentPageRequest(reviewId, "DESC", null, null, limit);

      given(reviewRepository.existsByIdAndDeletedAtIsNull(reviewId)).willReturn(true);

      List<Comment> mockComments = new ArrayList<>();
      for (int i = 0; i < limit + 1 ; i++) {
        Comment mockComment = mock(Comment.class);
        if (i == limit - 1) {
          given(mockComment.getId()).willReturn(UUID.randomUUID());
          given(mockComment.getCreatedAt()).willReturn(Instant.now());
        }
        mockComments.add(mockComment);
      }

      Comment lastCommentOfContent = mockComments.get(limit - 1);

      given(commentRepository.findAllByCursor(request)).willReturn(mockComments);
      given(commentRepository.countByReviewIdAndDeletedAtIsNull(eq(reviewId))).willReturn(10L);
      given(commentMapper.toDto(any(Comment.class))).willReturn(mock(CommentDto.class));

      // when
      var result = commentService.findAll(request);

      // then
      assertThat(result.content()).hasSize(limit);
      assertThat(result.hasNext()).isTrue();
      assertThat(result.nextCursor()).isEqualTo(lastCommentOfContent.getId().toString());
      assertThat(result.nextAfter()).isEqualTo(lastCommentOfContent.getCreatedAt());

      then(commentRepository).should().findAllByCursor(request);
    }

    @Test
    @DisplayName("댓글 목록 조회 실패 - 리뷰가 존재하지 않는 경우")
    void findAll_WhenReviewNotFound_ShouldThrowException() {
      // given
      UUID invalidReviewId = UUID.randomUUID();
      CommentPageRequest request = new CommentPageRequest(invalidReviewId, "DESC", null, null, 5);

      given(reviewRepository.existsByIdAndDeletedAtIsNull(eq(invalidReviewId))).willReturn(false);

      // when & then
      assertThatThrownBy(() -> commentService.findAll(request))
          .isInstanceOf(ReviewNotFoundException.class);

      then(commentRepository).should(never()).findAllByCursor(any());
    }
  }
}
