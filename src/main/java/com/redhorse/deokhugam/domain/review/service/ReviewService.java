package com.redhorse.deokhugam.domain.review.service;

import com.redhorse.deokhugam.domain.review.dto.CursorPageResponseReviewDto;
import com.redhorse.deokhugam.domain.review.dto.ReviewCreateRequest;
import com.redhorse.deokhugam.domain.review.dto.ReviewDto;
import com.redhorse.deokhugam.domain.review.dto.ReviewLikeDto;
import com.redhorse.deokhugam.domain.review.dto.ReviewSearchRequest;
import com.redhorse.deokhugam.domain.review.dto.ReviewUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface ReviewService {
  ReviewDto create(ReviewCreateRequest request);

  ReviewDto update(UUID reviewId, UUID userId, ReviewUpdateRequest request);

  void softDelete(UUID reviewId, UUID userId);

  void hardDelete(UUID reviewId, UUID userId);

  ReviewLikeDto like(UUID reviewId, UUID userId);

  CursorPageResponseReviewDto findAll(ReviewSearchRequest request,UUID requestUserId);
}
