package com.redhorse.deokhugam.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(GlobalException e) {
        log.warn("DiscodeitException: code={}, message={}", e.getErrorCode(), e.getErrorCode().getMessage());

        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(ErrorResponse.from(e));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception e) {
        log.error("Unexpected error: className={}, message={}", e.getClass().getSimpleName(), e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        Instant.now(),
                        "INTERNAL_SERVER_ERROR",
                        "예상치 못한 오류가 발생했습니다.",
                        Map.of(),
                        e.getClass().getSimpleName(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value()
                ));
    }
}
