package com.redhorse.deokhugam.domain.review.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class UserNotWriteReviewException extends ReviewException {

  public UserNotWriteReviewException(UUID userId) {
    super(ErrorCode.USER_NOT_WRITE_REVIEW, Map.of("userId", userId));
  }
}
