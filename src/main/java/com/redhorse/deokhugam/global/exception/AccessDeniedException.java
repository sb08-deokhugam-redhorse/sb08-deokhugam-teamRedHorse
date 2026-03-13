package com.redhorse.deokhugam.global.exception;

import java.util.Map;

public class AccessDeniedException extends GlobalException {


  public AccessDeniedException() {
    super(
        ErrorCode.UNAUTHORIZED_USER,
        Map.of(
            "header", "접근 권한 거부",
            "description", "본인의 데이터만 접근할 수 있습니다."
        )
    );
  }
}
