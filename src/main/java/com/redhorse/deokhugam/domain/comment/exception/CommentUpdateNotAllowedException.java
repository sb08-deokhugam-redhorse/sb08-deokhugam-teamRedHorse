package com.redhorse.deokhugam.domain.comment.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class CommentUpdateNotAllowedException extends CommentException {

  public CommentUpdateNotAllowedException(UUID commentId) {
    super(ErrorCode.COMMENT_UPDATE_NOT_ALLOWED, Map.of("commentId", commentId));
  }
}
