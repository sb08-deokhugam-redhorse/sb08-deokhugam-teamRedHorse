package com.redhorse.deokhugam.domain.user.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;
import java.util.Map;

public class UserDeletedNotYetException extends UserException {

  public UserDeletedNotYetException() {
    super(ErrorCode.HARD_DELETE_NOT_ALLOWED_YET, Map.of("description", "탈퇴 후 24시간이 지나지 않았습니다."));
  }
}