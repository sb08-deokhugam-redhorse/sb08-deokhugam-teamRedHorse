package com.redhorse.deokhugam.domain.comment.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;
import com.redhorse.deokhugam.global.exception.GlobalException;
import java.util.Map;

public class CommentException extends GlobalException {

  public CommentException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode, details);
  }
}
