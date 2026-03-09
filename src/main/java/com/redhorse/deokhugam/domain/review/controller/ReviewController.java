package com.redhorse.deokhugam.domain.review.controller;

import com.redhorse.deokhugam.domain.review.dto.CursorPageResponseReviewDto;
import com.redhorse.deokhugam.domain.review.dto.ReviewCreateRequest;
import com.redhorse.deokhugam.domain.review.dto.ReviewDto;
import com.redhorse.deokhugam.domain.review.dto.ReviewLikeDto;
import com.redhorse.deokhugam.domain.review.dto.ReviewSearchRequest;
import com.redhorse.deokhugam.domain.review.dto.ReviewUpdateRequest;
import com.redhorse.deokhugam.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

  private final ReviewService reviewService;

  @PostMapping
  public ResponseEntity<ReviewDto> create(
      @RequestBody @Valid ReviewCreateRequest request) {

    log.info("[Review-Controller] 생성 요청 시작: bookId = {}", request.bookId());
    ReviewDto dto = reviewService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  @PatchMapping("/{reviewId}")
  public ResponseEntity<ReviewDto> update(
      @PathVariable UUID reviewId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID userId,
      @RequestBody @Valid ReviewUpdateRequest request) {

    log.info("[Review-Controller] 수정 요청 시작: reviewId = {}", reviewId);
    ReviewDto dto = reviewService.update(reviewId, userId, request);
    return ResponseEntity.status(HttpStatus.OK).body(dto);
  }

  @DeleteMapping("/{reviewId}")
  public ResponseEntity<Void> delete(
      @PathVariable UUID reviewId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID userId) {

    log.info("[Review-Controller] 논리 삭제 요청 시작: reviewId = {}", reviewId);
    reviewService.softDelete(reviewId, userId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{reviewId}/hard")
  public ResponseEntity<Void> hardDelete(
      @PathVariable UUID reviewId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID userId) {

    log.info("[Review-Controller] 물리 삭제 요청 시작: reviewId = {}", reviewId);
    reviewService.hardDelete(reviewId, userId);
    return ResponseEntity.noContent().build();

  }

  @PostMapping("/{reviewId}/like")
  public ResponseEntity<ReviewLikeDto> like(
      @PathVariable UUID reviewId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID userId) {

    log.info("[Review-Controller] 좋아요 요청 시작: reviewId = {}", reviewId);
    ReviewLikeDto dto = reviewService.like(reviewId, userId);
    return ResponseEntity.status(HttpStatus.OK).body(dto);
  }

  @GetMapping()
  public ResponseEntity<CursorPageResponseReviewDto> findAll(
      @ParameterObject ReviewSearchRequest request,
      @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId) {

    log.info("[Review-Controller] 목록 조회 요청 시작: requestUserId = {}", requestUserId);
    CursorPageResponseReviewDto dto = reviewService.findAll(request, requestUserId);
    return ResponseEntity.status(HttpStatus.OK).body(dto);
  }

  @GetMapping("/{reviewId}")
  public ResponseEntity<ReviewDto> findById(
      @PathVariable UUID reviewId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId) {

    log.info("[Review-Controller] 상세 정보 조회 요청 시작: reviewId = {}", reviewId);
    ReviewDto dto = reviewService.findById(reviewId, requestUserId);
    return ResponseEntity.status(HttpStatus.OK).body(dto);

  }

}
