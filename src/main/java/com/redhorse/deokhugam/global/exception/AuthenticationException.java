package com.redhorse.deokhugam.global.exception;

import java.util.Map;

public class AuthenticationException extends GlobalException {

  public AuthenticationException() {
    super(
        ErrorCode.UNAUTHORIZED_USER,
        Map.of(
            "header", "인증 헤더 확인이 필요합니다.",
            "description", "인증을 위해 유효한 사용자 ID가 필요합니다."
        )
    );
  }
}
