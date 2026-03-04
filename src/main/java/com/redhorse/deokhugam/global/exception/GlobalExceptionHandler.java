package com.redhorse.deokhugam.global.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(GlobalException.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(GlobalException e) {
    log.warn("DiscodeitException: code={}, message={}", e.getErrorCode(),
        e.getErrorCode().getMessage());

    return ResponseEntity.status(e.getErrorCode().getStatus())
        .body(ErrorResponse.from(e));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
    Map<String, Object> details = new HashMap<>();

    for (FieldError error : e.getBindingResult().getFieldErrors()) {
      // 이미 담긴 필드는 무시 (첫 번째 에러만 유지)
      details.putIfAbsent(
          error.getField(),
          error.getDefaultMessage() != null ?
              error.getDefaultMessage() : "올바르지 않은 입력값입니다."
      );
    }

    log.warn("입력값 에러: details={}", details);

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(
            Instant.now(),
            "VALIDATION_FAILED",
            "입력값 검증에 실패했습니다.",
            details,
            e.getClass().getSimpleName(),
            HttpStatus.BAD_REQUEST.value()
        ));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAllException(Exception e) {
    log.error("Unexpected error: className={}, message={}", e.getClass().getSimpleName(),
        e.getMessage());

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
