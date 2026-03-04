package com.redhorse.deokhugam.domain.comment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhorse.deokhugam.domain.comment.dto.CommentCreateRequest;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import com.redhorse.deokhugam.domain.comment.service.CommentService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
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

  @Test
  @DisplayName("댓글 등록 요청이 성공적으로 처리되어야 한다.")
  void create_Success() throws Exception {
    // given
    UUID reviewId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    CommentCreateRequest request = new CommentCreateRequest(reviewId, userId, "하이루");

    CommentDto responseDto = new CommentDto(
        UUID.randomUUID(), reviewId, userId, "감자", "하이루",
        Instant.now().toString(), Instant.now().toString()
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
  void create_Fail_ReviewNotFound() throws Exception {
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
  void create_Fail_UserNotFound() throws Exception {
    // given
    CommentCreateRequest request = new CommentCreateRequest(UUID.randomUUID(), UUID.randomUUID(), "하이루");

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
  void create_Fail_InvalidInput() throws Exception {
    // given
    CommentCreateRequest invalidRequest = new CommentCreateRequest(null, null, "");

    // when & then
    mockMvc.perform(post("/api/comments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }
}