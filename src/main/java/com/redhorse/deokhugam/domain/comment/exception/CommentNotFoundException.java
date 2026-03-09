package com.redhorse.deokhugam.domain.comment.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class CommentNotFoundException extends CommentException {

  public CommentNotFoundException(UUID commentId) {
    super(ErrorCode.COMMENT_NOT_FOUND, Map.of("commentId", commentId));
  }
}
