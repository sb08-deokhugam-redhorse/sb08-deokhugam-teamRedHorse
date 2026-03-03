package com.redhorse.deokhugam.global.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        Instant timestamp,
        String code,
        String message,
        Map<String, Object> details,
        String exceptionType, // 발생한 예외의 클래스 이름
        int status // HTTP 상태 코드
) {
    public static ErrorResponse from(GlobalException e) {
        return new ErrorResponse(
                e.getTimestamp(),
                e.getErrorCode().name(),
                e.getErrorCode().getMessage(),
                e.getDetails(),
                e.getClass().getSimpleName(),
                e.getErrorCode().getStatus()
        );
    }
}
