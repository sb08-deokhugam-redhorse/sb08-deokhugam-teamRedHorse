package com.redhorse.deokhugam.domain.comment.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class CommentDeleteNotAllowedException extends CommentException {

  public CommentDeleteNotAllowedException(UUID commentId) {
    super(ErrorCode.COMMENT_DELETE_NOT_ALLOWED, Map.of("commentId", commentId));
  }
}
