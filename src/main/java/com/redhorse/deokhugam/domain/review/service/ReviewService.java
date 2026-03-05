package com.redhorse.deokhugam.domain.review.service;

import com.redhorse.deokhugam.domain.review.dto.ReviewCreateRequest;
import com.redhorse.deokhugam.domain.review.dto.ReviewDto;
import com.redhorse.deokhugam.domain.review.dto.ReviewUpdateRequest;
import java.util.UUID;

public interface ReviewService {
  ReviewDto create(ReviewCreateRequest request);

  ReviewDto update(UUID reviewId, UUID userId, ReviewUpdateRequest request);
}
