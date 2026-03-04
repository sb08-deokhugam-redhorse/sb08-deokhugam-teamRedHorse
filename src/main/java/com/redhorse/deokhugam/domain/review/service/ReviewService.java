package com.redhorse.deokhugam.domain.review.service;

import com.redhorse.deokhugam.domain.review.dto.ReviewCreateRequest;
import com.redhorse.deokhugam.domain.review.dto.ReviewDto;

public interface ReviewService {
  ReviewDto create(ReviewCreateRequest request);
}
