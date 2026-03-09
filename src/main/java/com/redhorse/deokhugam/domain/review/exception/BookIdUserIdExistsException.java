package com.redhorse.deokhugam.domain.review.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class BookIdUserIdExistsException extends ReviewException {

  public BookIdUserIdExistsException(UUID bookId, UUID userId) {
    super(ErrorCode.BOOKID_USERID_EXISTS, Map.of("bookId", bookId, "userId", userId));
  }
}
