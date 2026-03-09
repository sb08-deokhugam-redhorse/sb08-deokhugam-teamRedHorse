package com.redhorse.deokhugam.domain.review.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;
import com.redhorse.deokhugam.global.exception.GlobalException;
import java.util.Map;

public class ReviewException extends GlobalException {

  public ReviewException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode, details);
  }
}
