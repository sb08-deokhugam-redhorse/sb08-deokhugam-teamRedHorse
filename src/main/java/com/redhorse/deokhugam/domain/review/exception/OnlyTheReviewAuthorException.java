package com.redhorse.deokhugam.domain.review.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class OnlyTheReviewAuthorException extends ReviewException {

  public OnlyTheReviewAuthorException(UUID userId) {
    super(ErrorCode.ONLY_THE_REVIEW_AUTHOR, Map.of("userId", userId));
  }
}
