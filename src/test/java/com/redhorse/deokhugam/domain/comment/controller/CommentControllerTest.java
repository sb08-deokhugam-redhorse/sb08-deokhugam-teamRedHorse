package com.redhorse.deokhugam.domain.comment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhorse.deokhugam.domain.alarm.service.AlarmService;
import com.redhorse.deokhugam.domain.comment.dto.CommentCreateRequest;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import com.redhorse.deokhugam.domain.comment.dto.CommentPageRequest;
import com.redhorse.deokhugam.domain.comment.dto.CommentUpdateRequest;
import com.redhorse.deokhugam.domain.comment.dto.CursorPageResponseCommentDto;
import com.redhorse.deokhugam.domain.comment.exception.CommentDeleteNotAllowedException;
import com.redhorse.deokhugam.domain.comment.exception.CommentNotFoundException;
import com.redhorse.deokhugam.domain.comment.exception.CommentUpdateNotAllowedException;
import com.redhorse.deokhugam.domain.comment.service.CommentService;
import com.redhorse.deokhugam.domain.review.exception.ReviewNotFoundException;
import com.redhorse.deokhugam.domain.user.exception.UserNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(CommentController.class)
@DisplayName("CommentController 테스트")
class CommentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private CommentService commentService;

  @MockitoBean
  private AlarmService alarmService;

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

      verify(alarmService, times(1)).createCommentAlarm(any(CommentDto.class));
    }

    @Test
    @DisplayName("댓글 등록 성공 - 알림 서비스에서 예외가 발생해도 댓글 생성은 유지되어야 한다.")
    void create_WhenAlarmServiceFails_ShouldStillReturnCreated() throws Exception {
      // given
      UUID reviewId = UUID.randomUUID();
      UUID userId = UUID.randomUUID();
      CommentCreateRequest request = new CommentCreateRequest(reviewId, userId, "알람실패테스트");
      CommentDto responseDto = new CommentDto(
          UUID.randomUUID(), reviewId, userId, "감자", "알람실패테스트",
          Instant.now(), Instant.now()
      );

      given(commentService.create(any(CommentCreateRequest.class))).willReturn(responseDto);

      doThrow(new RuntimeException("알람 서버 장애"))
          .when(alarmService).createCommentAlarm(any(CommentDto.class));

      // when & then
      mockMvc.perform(post("/api/comments")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.content").value("알람실패테스트"));

      verify(alarmService, times(1)).createCommentAlarm(any(CommentDto.class));
    }

    @Test
    @DisplayName("댓글 등록 실패 - 존재하지 않는 리뷰 ID인 경우 404 Not Found를 반환한다.")
    void create_WhenReviewNotFound_ShouldThrowException() throws Exception {
      // given
      UUID invalidReviewId = UUID.randomUUID();
      UUID userId = UUID.randomUUID();
      CommentCreateRequest request = new CommentCreateRequest(invalidReviewId, userId, "하이루");

      given(commentService.create(any(CommentCreateRequest.class)))
          .willThrow(new ReviewNotFoundException(invalidReviewId));

      // when & then
      mockMvc.perform(post("/api/comments")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("댓글 등록 실패 - 존재하지 않는 사용자 ID인 경우 404 Not Found를 반환한다.")
    void create_WhenUserNotFound_ShouldThrowException() throws Exception {
      // given
      UUID reviewId = UUID.randomUUID();
      UUID invalidUserID = UUID.randomUUID();
      CommentCreateRequest request = new CommentCreateRequest(reviewId, invalidUserID,
          "하이루");

      given(commentService.create(any(CommentCreateRequest.class)))
          .willThrow(new UserNotFoundException(invalidUserID));

      // when & then
      mockMvc.perform(post("/api/comments")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("입력값 검증 실패 - valid 검증을 실패하면 400 Bad Request를 반환한다.")
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
    @DisplayName("댓글 수정 실패 - 작성자가 아닌 유저가 요청하면 403 Forbidden을 반환한다")
    void update_WhenUserIsNotAuthor_ShouldThrowException() throws Exception {
      // given
      UUID commentId = UUID.randomUUID();
      UUID requestUserId = UUID.randomUUID();
      CommentUpdateRequest request = new CommentUpdateRequest("댓글 수정 테스트");

      given(
          commentService.update(eq(commentId), eq(requestUserId), any(CommentUpdateRequest.class)))
          .willThrow(new CommentUpdateNotAllowedException(commentId));

      // when & then
      mockMvc.perform(patch("/api/comments/{commentId}", commentId)
              .header("Deokhugam-Request-User-ID", requestUserId.toString())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isForbidden());
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
    @DisplayName("댓글 단건 조회 실패 - 존재하지 않은 댓글 Id인 경우 404 Not Found를 반환한다.")
    void find_WhenCommentNotFound_ShouldThrowException() throws Exception {
      // given
      UUID invalidCommentId = UUID.randomUUID();

      given(commentService.find(eq(invalidCommentId)))
          .willThrow(new CommentNotFoundException(invalidCommentId));

      // when & then
      mockMvc.perform(get("/api/comments/{commentId}", invalidCommentId)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
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
    @DisplayName("댓글 논리 삭제 실패 - 존재하지 않은 댓글 Id인 경우 404 Not Found를 반환한다.")
    void softDelete_WhenCommentNotFound_ShouldThrowException() throws Exception {
      // given
      UUID commentId = UUID.randomUUID();
      UUID requestUserId = UUID.randomUUID();

      doThrow(new CommentNotFoundException(commentId))
          .when(commentService).softDelete(eq(commentId), eq(requestUserId));

      // when & then
      mockMvc.perform(delete("/api/comments/{commentId}", commentId)
              .header("Deokhugam-Request-User-ID", requestUserId.toString()))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("댓글 논리 삭제 실패 - 작성자가 아닌 유저가 요청하면 403 Forbidden을 반환한다.")
    void softDelete_WhenUserIsNotAuthor_ShouldThrowException() throws Exception {
      // given
      UUID commentId = UUID.randomUUID();
      UUID requestUserId = UUID.randomUUID();

      doThrow(new CommentDeleteNotAllowedException(commentId))
          .when(commentService).softDelete(eq(commentId), eq(requestUserId));

      // when & then
      mockMvc.perform(delete("/api/comments/{commentId}", commentId)
              .header("Deokhugam-Request-User-ID", requestUserId.toString()))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("댓글 논리 삭제 실패 - 요청 헤더가 누락된 경우 400 Bad Request를 반환한다")
    void softDelete_WhenHeaderMissing_ShouldReturnBadRequest() throws Exception {
      // given
      UUID commentId = UUID.randomUUID();

      // when & then
      mockMvc.perform(delete("/api/comments/{commentId}", commentId))
          .andExpect(status().isBadRequest());
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
    @DisplayName("댓글 물리 삭제 실패 - 존재하지 않은 댓글 Id인 경우 404 Not Found를 반환한다.")
    void hardDelete_WhenCommentNotFound_ShouldThrowException() throws Exception {
      // given
      UUID commentId = UUID.randomUUID();
      UUID requestUserId = UUID.randomUUID();

      doThrow(new CommentNotFoundException(commentId))
          .when(commentService).hardDelete(eq(commentId), eq(requestUserId));

      // when & then
      mockMvc.perform(delete("/api/comments/{commentId}/hard", commentId)
              .header("Deokhugam-Request-User-ID", requestUserId.toString()))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("댓글 물리 삭제 실패 - 작성자가 아닌 유저가 요청하면 403 Forbidden을 반환한다.")
    void hardDelete_WhenUserIsNotAuthor_ShouldThrowException() throws Exception {
      // given
      UUID commentId = UUID.randomUUID();
      UUID requestUserId = UUID.randomUUID();

      doThrow(new CommentDeleteNotAllowedException(commentId))
          .when(commentService).hardDelete(eq(commentId), eq(requestUserId));

      // when & then
      mockMvc.perform(delete("/api/comments/{commentId}/hard", commentId)
              .header("Deokhugam-Request-User-ID", requestUserId.toString()))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("댓글 물리 삭제 실패 - 요청 헤더가 누락된 경우 400 Bad Request를 반환한다")
    void hardDelete_WhenHeaderMissing_ShouldReturnBadRequest() throws Exception {
      // given
      UUID commentId = UUID.randomUUID();

      // when & then
      mockMvc.perform(delete("/api/comments/{commentId}/hard", commentId))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("댓글 목록 조회 관련 테스트")
  class findAllCommentTests {

    @Test
    @DisplayName("댓글 목록 조회 요청이 성공적으로 처리되어야 한다.")
    void findAll_Success() throws Exception {
      // given
      UUID reviewId = UUID.randomUUID();
      CommentPageRequest request = new CommentPageRequest(reviewId, "DESC", null, null, 5);

      CursorPageResponseCommentDto responseDto = new CursorPageResponseCommentDto(
          List.of(), null, null, 5, 3L, false
      );

      given(commentService.findAll(any(CommentPageRequest.class))).willReturn(responseDto);

      // when & then
      mockMvc.perform(get("/api/comments")
              .param("reviewId", reviewId.toString())
              .param("direction", "DESC")
              .param("limit", "5"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.size").value(5))
          .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    @DisplayName("댓글 목록 조회 실패 - reviewId가 누락되면 400 BadRequest를 반환한다")
    void findAll_WhenInvalidRequest_ShouldReturnBadRequest() throws Exception {
      // given & when & then
      // review Id 파라미터 누락
      mockMvc.perform(get("/api/comments")
              .param("direction", "DESC")
              .param("limit", "5"))
          .andExpect(status().isBadRequest());
    }
  }
}