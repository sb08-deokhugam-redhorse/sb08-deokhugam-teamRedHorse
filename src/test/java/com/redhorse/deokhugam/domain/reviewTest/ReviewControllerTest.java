package com.redhorse.deokhugam.domain.reviewTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhorse.deokhugam.domain.review.controller.ReviewController;
import com.redhorse.deokhugam.domain.review.dto.ReviewCreateRequest;
import com.redhorse.deokhugam.domain.review.dto.ReviewDto;
import com.redhorse.deokhugam.domain.review.service.ReviewService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReviewController.class)
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
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}
