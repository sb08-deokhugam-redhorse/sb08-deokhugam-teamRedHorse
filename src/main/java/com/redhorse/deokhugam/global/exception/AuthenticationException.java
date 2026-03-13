package com.redhorse.deokhugam.global.exception;

import java.util.Map;

public class AuthenticationException extends GlobalException {

  public AuthenticationException() {
    super(
        ErrorCode.UNAUTHORIZED_USER,
        Map.of(
            "header", "요청 헤더 누락 또는 오류",
            "description", "필수 인증 헤더가 없거나, 올바른 식별자(UUID) 형식이 아닙니다."
        )
    );
  }
}
