package com.redhorse.deokhugam.domain.user.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;
import java.util.Map;

public class UserLoginFailedException extends UserException {

  // 로그인 실패시 아이디, 비번 둘 중 뭐가 틀렸는지 알려주지 않음
  public UserLoginFailedException() {
    super(ErrorCode.LOGIN_FAILED, Map.of());
  }
}
