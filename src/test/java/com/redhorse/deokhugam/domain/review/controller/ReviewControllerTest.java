package com.redhorse.deokhugam.domain.review.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhorse.deokhugam.domain.review.dto.ReviewCreateRequest;
import com.redhorse.deokhugam.domain.review.dto.ReviewDto;
import com.redhorse.deokhugam.domain.review.dto.ReviewLikeDto;
import com.redhorse.deokhugam.domain.review.dto.ReviewUpdateRequest;
import com.redhorse.deokhugam.domain.review.exception.ReviewNotFoundException;
import com.redhorse.deokhugam.domain.review.exception.OnlyTheReviewAuthorException;
import com.redhorse.deokhugam.domain.review.service.ReviewService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReviewController.class)
@ActiveProfiles("test")
public class ReviewControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ReviewService reviewService;

  @Test
  @DisplayName("리뷰 등록 성공 테스트")
  void createReview_Success() throws Exception {
    // given
    UUID bookId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    ReviewCreateRequest request = new ReviewCreateRequest(
        bookId,
        userId,
        "테스트",
        5
    );
    UUID reviewId = UUID.randomUUID();
    ReviewDto reviewDto = new ReviewDto(
        reviewId,
        bookId,
        "testBook",
        "testUrl",
        userId,
        "testName",
        "테스트",
        5,
        0,
        0,
        false,
        Instant.now(),
        Instant.now()
    );

    given(reviewService.create(any(ReviewCreateRequest.class)))
        .willReturn(reviewDto);

    // when & then
    mockMvc.perform(post("/api/reviews")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Deokhugam-Request-User-ID", userId)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(reviewId.toString()))
        .andExpect(jsonPath("$.bookId").value(bookId.toString()))
        .andExpect(jsonPath("$.userId").value(userId.toString()))
        .andExpect(jsonPath("$.content").value("테스트"))
        .andExpect(jsonPath("$.rating").value(5));
  }

  @Test
  @DisplayName("리뷰 등록 실패 테스트 - 평점이 5를 초과할 경우")
  void createReview_Failure() throws Exception {
    // given
    UUID bookId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    ReviewCreateRequest request = new ReviewCreateRequest(
        bookId,
        userId,
        "테스트",
        8
    );

    // when & then
    mockMvc.perform(post("/api/reviews")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Deokhugam-Request-User-ID", userId)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("리뷰 수정 성공 테스트")
  void updateReview_Success() throws Exception {
    // given
    UUID reviewId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    UUID bookId = UUID.randomUUID();
    ReviewUpdateRequest request = new ReviewUpdateRequest("update", 3);

    ReviewDto reviewDto = new ReviewDto(
        reviewId,
        bookId,
        "testBook",
        "testUrl",
        userId,
        "testName",
        "update",
        3,
        0,
        0,
        false,
        Instant.now(),
        Instant.now()
    );

    given(reviewService.update(eq(reviewId), eq(userId), any(ReviewUpdateRequest.class)))
        .willReturn(reviewDto);

    // when & then
    mockMvc.perform(patch("/api/reviews/{reviewId}", reviewId)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Deokhugam-Request-User-ID", userId)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(reviewId.toString()))
        .andExpect(jsonPath("$.userId").value(userId.toString()))
        .andExpect(jsonPath("$.content").value("update"))
        .andExpect(jsonPath("$.rating").value(3));
  }

  @Test
  @DisplayName("리뷰 수정 실패 테스트 - 존재하지 않은 리뷰일 경우")
  void updateReview_Failure() throws Exception {
    // given
    UUID reviewId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    ReviewUpdateRequest request = new ReviewUpdateRequest("update", 3);

    given(reviewService.update(eq(reviewId), eq(userId), any(ReviewUpdateRequest.class)))
        .willThrow(new ReviewNotFoundException(reviewId));

    //when & then
    mockMvc.perform(patch("/api/reviews/{reviewId}", reviewId)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Deokhugam-Request-User-ID", userId)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("리뷰 논리 삭제 성공 테스트")
  void deleteReview_Success() throws Exception {
    // given
    UUID reviewId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    willDoNothing().given(reviewService).softDelete(eq(reviewId), eq(userId));

    // when & then
    mockMvc.perform(delete("/api/reviews/{reviewId}", reviewId)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Deokhugam-Request-User-ID", userId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("리뷰 논리 삭제 실패 테스트 - 리뷰 작성자가 아닌 사람이 삭제할 경우")
  void deleteReview_Failure() throws Exception {
    // given
    UUID reviewId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    willThrow(new OnlyTheReviewAuthorException(userId))
        .given(reviewService).softDelete(eq(reviewId), eq(userId));

    // when & then
    mockMvc.perform(delete("/api/reviews/{reviewId}", reviewId)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Deokhugam-Request-User-ID", userId))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("리뷰 물리 삭제 성공 테스트")
  void deleteHardReview_Success() throws Exception {
    // given
    UUID reviewId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    willDoNothing().given(reviewService).hardDelete(eq(reviewId), eq(userId));

    // when & then
    mockMvc.perform(delete("/api/reviews/{reviewId}/hard", reviewId)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Deokhugam-Request-User-ID", userId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("리뷰 물리 삭제 실패 테스트 - 존재하지 않는 리뷰일 경우")
  void deleteHardReview_Failure() throws Exception {
    UUID reviewId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    willThrow(new ReviewNotFoundException(reviewId))
        .given(reviewService).hardDelete(eq(reviewId), eq(userId));

    // when & then
    mockMvc.perform(delete("/api/reviews/{reviewId}/hard", reviewId)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Deokhugam-Request-User-ID", userId))
        .andExpect(status().isNotFound());

  }

  @Test
  @DisplayName("리뷰 좋아요 성공 테스트")
  void createReviewLike_Success() throws Exception {
    // given
    UUID reviewId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    ReviewLikeDto request = new ReviewLikeDto(reviewId, userId, true);

    given(reviewService.like(reviewId, userId)).willReturn(request);

    // when & then
    mockMvc.perform(post("/api/reviews/{reviewId}/like", reviewId)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Deokhugam-Request-User-ID", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.like").value(true));
  }

  @Test
  @DisplayName("리뷰 좋아요 실패 테스트 - 존재하지 않는 리뷰일 경우")
  void createReviewLike_Failure() throws Exception {
    // given
    UUID reviewId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    given(reviewService.like(eq(reviewId), eq(userId)))
        .willThrow(new ReviewNotFoundException(reviewId));

    // when & then
    mockMvc.perform(post("/api/reviews/{reviewId}/like", reviewId)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Deokhugam-Request-User-ID", userId))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("리뷰 상세 조회 성공 테스트 ")
  void findByIdReview_Success() throws Exception {
    // given
    UUID reviewId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    UUID bookId = UUID.randomUUID();

    ReviewDto request = new ReviewDto(
        reviewId,
        bookId,
        "testBook",
        "testUrl",
        userId,
        "testName",
        "content",
        3,
        0,
        0,
        false,
        Instant.now(),
        Instant.now()
    );

    given(reviewService.findById(eq(reviewId), eq(userId)))
        .willReturn(request);

    // when & then
    mockMvc.perform(get("/api/reviews/{reviewId}", reviewId)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Deokhugam-Request-User-ID", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(reviewId.toString()))
        .andExpect(jsonPath("$.content").value("content"))
        .andExpect(jsonPath("$.rating").value(3));

  }

  @Test
  @DisplayName("리뷰 상세 조회 실패 테스트 - 존재하지 않는 리뷰일 경우")
  void findByIdReview_Failure() throws Exception {
    // given
    UUID reviewId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    willThrow(new ReviewNotFoundException(reviewId))
        .given(reviewService).findById(eq(reviewId), eq(userId));

    // when & then
    mockMvc.perform(get("/api/reviews/{reviewId}", reviewId)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Deokhugam-Request-User-ID", userId))
        .andExpect(status().isNotFound());

  }
}
