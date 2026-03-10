package com.redhorse.deokhugam.domain.user.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class UserNotFoundException extends UserException {

  public UserNotFoundException(UUID userId) {
    super(ErrorCode.USER_NOT_FOUND, Map.of("user", userId));
  }
}