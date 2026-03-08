package com.redhorse.deokhugam.domain.review.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class ReviewNotFoundException extends ReviewException {

  public ReviewNotFoundException(UUID reviewId) {
    super(ErrorCode.REVIEW_NOT_FOUND, Map.of("reviewId", reviewId));
  }
}
