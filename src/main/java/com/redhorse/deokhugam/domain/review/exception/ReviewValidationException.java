package com.redhorse.deokhugam.domain.review.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;
import java.util.Map;

public class ReviewValidationException extends ReviewException {

  public ReviewValidationException(String message) {
    super(ErrorCode.REVIEW_VALIDATION, Map.of("message",message));
  }
}
