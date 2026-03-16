package com.redhorse.deokhugam.global.exception;

import java.util.Map;

public class InvalidDirectionException extends CommonException {

  public InvalidDirectionException(String direction) {
    super(ErrorCode.INVALID_DIRECTION, Map.of("direction", direction));
  }
}
