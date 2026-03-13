package com.redhorse.deokhugam.global.exception;

import java.util.Map;

public class AuthenticationException extends GlobalException {

  public AuthenticationException() {
    super(
        ErrorCode.AUTHENTICATION_FAILED,
        Map.of(
            "header", "인증 실패",
            "description", "필수 인증 헤더가 없거나 올바른 UUID 형식이 아닙니다."
        )
    );
  }
}
