package com.redhorse.deokhugam.domain.comment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhorse.deokhugam.domain.comment.dto.CommentCreateRequest;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import com.redhorse.deokhugam.domain.comment.dto.CommentUpdateRequest;
import com.redhorse.deokhugam.domain.comment.service.CommentService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CommentController.class)
@DisplayName("CommentController 테스트")
class CommentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private CommentService commentService;

  @Nested
  @DisplayName("댓글 등록 관련 테스트")
  class createCommentsTests {

    @Test
    @DisplayName("댓글 등록 요청이 성공적으로 처리되어야 한다.")
    void create_Success() throws Exception {
      // given
      UUID reviewId = UUID.randomUUID();
      UUID userId = UUID.randomUUID();
      CommentCreateRequest request = new CommentCreateRequest(reviewId, userId, "하이루");

      CommentDto responseDto = new CommentDto(
          UUID.randomUUID(), reviewId, userId, "감자", "하이루",
          Instant.now(), Instant.now()
      );

      given(commentService.create(any(CommentCreateRequest.class))).willReturn(responseDto);

      // when & then
      mockMvc.perform(post("/api/comments")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.content").value("하이루"))
          .andExpect(jsonPath("$.userNickname").value("감자"));
    }

    @Test
    @DisplayName("댓글 등록 실패 - 존재하지 않는 리뷰 ID인 경우 500 반환")
    void create_WhenReviewNotFound_ShouldThrowException() throws Exception {
      // given
      UUID invalidReviewId = UUID.randomUUID();
      UUID userId = UUID.randomUUID();
      CommentCreateRequest request = new CommentCreateRequest(invalidReviewId, userId, "하이루");

      given(commentService.create(any(CommentCreateRequest.class)))
          .willThrow(new IllegalArgumentException("Review Not Found"));

      // when & then
      mockMvc.perform(post("/api/comments")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("댓글 등록 실패 - 존재하지 않는 사용자 ID인 경우 500 반환")
    void create_WhenUserNotFound_ShouldThrowException() throws Exception {
      // given
      CommentCreateRequest request = new CommentCreateRequest(UUID.randomUUID(), UUID.randomUUID(),
          "하이루");

      given(commentService.create(any(CommentCreateRequest.class)))
          .willThrow(new IllegalArgumentException("User Not Found"));

      // when & then
      mockMvc.perform(post("/api/comments")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("입력값 검증 실패 시 - 400 Bad Request")
    void create_InvalidInput_ShouldThrowException() throws Exception {
      // given
      CommentCreateRequest invalidRequest = new CommentCreateRequest(null, null, "");

      // when & then
      mockMvc.perform(post("/api/comments")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(invalidRequest)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("댓글 수정 관련 테스트")
  class updateCommentsTests {

    @Test
    @DisplayName("댓글 수정 요청이 성공적으로 처리되어야 한다.")
    void update_Success() throws Exception {
      // given
      UUID commentId = UUID.randomUUID();
      UUID requestUserId = UUID.randomUUID();
      CommentUpdateRequest commentReq = new CommentUpdateRequest("댓글 수정 테스트");

      CommentDto responseDto = new CommentDto(
          commentId, UUID.randomUUID(), requestUserId, "감자", "댓글 수정 테스트",
          Instant.now(), Instant.now()
      );

      given(commentService.update(eq(commentId), eq(requestUserId),
          any(CommentUpdateRequest.class))).willReturn(responseDto);

      // when & then
      mockMvc.perform(patch("/api/comments/{commentId}", commentId)
              .header("Deokhugam-Request-User-ID", requestUserId.toString())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(commentReq)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(commentId.toString()))
          .andExpect(jsonPath("$.content").value("댓글 수정 테스트"))
          .andExpect(jsonPath("$.userId").value(requestUserId.toString()));
    }

    @Test
    @DisplayName("댓글 수정 실패 - 작성자가 아닌 유저가 요청하면 500을 반환한다")
    void update_WhenUserIsNotAuthor_ShouldThrowException() throws Exception {
      // given
      UUID commentId = UUID.randomUUID();
      UUID requestUserId = UUID.randomUUID();
      CommentUpdateRequest request = new CommentUpdateRequest("댓글 수정 테스트");

      given(
          commentService.update(eq(commentId), eq(requestUserId), any(CommentUpdateRequest.class)))
          .willThrow(new IllegalArgumentException("자신이 작성한 댓글만 수정할 수 있습니다."));

      // when & then
      mockMvc.perform(patch("/api/comments/{commentId}", commentId)
              .header("Deokhugam-Request-User-ID", requestUserId.toString())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isInternalServerError());
    }
  }

  @Nested
  @DisplayName("댓글 단건 조회 관련 테스트")
  class findCommentsTests {

    @Test
    @DisplayName("댓글 단건 조회 요청이 성공적으로 처리되어야 한다.")
    void find_Success() throws Exception {
      // given
      UUID commentId = UUID.randomUUID();
      CommentDto responseDto = new CommentDto(
          commentId, UUID.randomUUID(), UUID.randomUUID(), "감자", "댓글 수정 테스트",
          Instant.now(), Instant.now()
      );

      given(commentService.find(eq(commentId))).willReturn(responseDto);

      // when & then
      mockMvc.perform(get("/api/comments/{commentId}", commentId)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(commentId.toString()))
          .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("댓글 단건 조회 실패 - 존재하지 않은 댓글 Id인 경우 500을 반환한다.")
    void find_WhenCommentNotFound_ShouldThrowException() throws Exception {
      // given
      UUID invalidCommentId = UUID.randomUUID();

      given(commentService.find(eq(invalidCommentId)))
          .willThrow(new IllegalArgumentException("Comment Not Found"));

      // when & then
      mockMvc.perform(get("/api/comments/{commentId}", invalidCommentId)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isInternalServerError());
    }
  }

  @Nested
  @DisplayName("댓글 논리 삭제 관련 테스트")
  class softDeleteCommentTests {

    @Test
    @DisplayName("댓글 논리 삭제 요청이 성공적으로 처리되어야 한다.")
    void softDelete_Success() throws Exception {
      // given
      UUID commentId = UUID.randomUUID();
      UUID requestUserId = UUID.randomUUID();

      doNothing().when(commentService).softDelete(eq(commentId), eq(requestUserId));

      // when & then
      mockMvc.perform(delete("/api/comments/{commentId}", commentId)
              .header("Deokhugam-Request-User-ID", requestUserId.toString()))
          .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("댓글 논리 삭제 실패 - 존재하지 않은 댓글 Id인 경우 500을 반환한다.")
    void softDelete_WhenCommentNotFound_ShouldThrowException() throws Exception {
      // given
      UUID commentId = UUID.randomUUID();
      UUID requestUserId = UUID.randomUUID();

      doThrow(new IllegalArgumentException("Comment Not Found"))
          .when(commentService).softDelete(eq(commentId), eq(requestUserId));

      // when & then
      mockMvc.perform(delete("/api/comments/{commentId}", commentId)
              .header("Deokhugam-Request-User-ID", requestUserId.toString()))
          .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("댓글 논리 삭제 실패 - 작성자가 아닌 유저가 요청하면 500을 반환한다.")
    void softDelete_WhenUserIsNotAuthor_ShouldThrowException() throws Exception {
      // given
      UUID commentId = UUID.randomUUID();
      UUID requestUserId = UUID.randomUUID();

      doThrow(new IllegalArgumentException("자신이 작성한 댓글만 삭제할 수 있습니다."))
          .when(commentService).softDelete(eq(commentId), eq(requestUserId));

      // when & then
      mockMvc.perform(delete("/api/comments/{commentId}", commentId)
              .header("Deokhugam-Request-User-ID", requestUserId.toString()))
          .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("댓글 논리 삭제 실패 - 요청 헤더가 누락된 경우 400을 반환한다")
    void softDelete_WhenHeaderMissing_ShouldReturnBadRequest() throws Exception {
      // given
      UUID commentId = UUID.randomUUID();

      // when & then
      mockMvc.perform(delete("/api/comments/{commentId}", commentId))
          .andExpect(status().isInternalServerError());
    }
  }

  @Nested
  @DisplayName("댓글 물리 삭제 관련 테스트")
  class hardDeleteCommentTests {

    @Test
    @DisplayName("댓글 물리 삭제 요청이 성공적으로 처리되어야 한다.")
    void hardDelete_Success() throws Exception {
      // given
      UUID commentId = UUID.randomUUID();
      UUID requestUserId = UUID.randomUUID();

      doNothing().when(commentService).hardDelete(eq(commentId), eq(requestUserId));

      // when & then
      mockMvc.perform(delete("/api/comments/{commentId}/hard", commentId)
              .header("Deokhugam-Request-User-ID", requestUserId.toString()))
          .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("댓글 물리 삭제 실패 - 존재하지 않은 댓글 Id인 경우 500을 반환한다.")
    void hardDelete_WhenCommentNotFound_ShouldThrowException() throws Exception {
      // given
      UUID commentId = UUID.randomUUID();
      UUID requestUserId = UUID.randomUUID();

      doThrow(new IllegalArgumentException("Comment Not Found"))
          .when(commentService).hardDelete(eq(commentId), eq(requestUserId));

      // when & then
      mockMvc.perform(delete("/api/comments/{commentId}/hard", commentId)
              .header("Deokhugam-Request-User-ID", requestUserId.toString()))
          .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("댓글 물리 삭제 실패 - 작성자가 아닌 유저가 요청하면 500을 반환한다.")
    void hardDelete_WhenUserIsNotAuthor_ShouldThrowException() throws Exception {
      // given
      UUID commentId = UUID.randomUUID();
      UUID requestUserId = UUID.randomUUID();

      doThrow(new IllegalArgumentException("자신이 작성한 댓글만 삭제할 수 있습니다."))
          .when(commentService).hardDelete(eq(commentId), eq(requestUserId));

      // when & then
      mockMvc.perform(delete("/api/comments/{commentId}/hard", commentId)
              .header("Deokhugam-Request-User-ID", requestUserId.toString()))
          .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("댓글 물리 삭제 실패 - 요청 헤더가 누락된 경우 400을 반환한다")
    void hardDelete_WhenHeaderMissing_ShouldReturnBadRequest() throws Exception {
      // given
      UUID commentId = UUID.randomUUID();

      // when & then
      mockMvc.perform(delete("/api/comments/{commentId}/hard", commentId))
          .andExpect(status().isInternalServerError());
    }
  }
}