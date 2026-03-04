package com.redhorse.deokhugam.domain.user.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;
import java.util.Map;

public class UserDuplicateException extends UserException {

  public UserDuplicateException(String email) {
    super(ErrorCode.USER_DUPLICATE, Map.of("email", email));
  }
}
