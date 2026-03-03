package com.redhorse.deokhugam.global.exception;

import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
public class GlobalException extends RuntimeException {
    private final Instant timestamp;
    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    public GlobalException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode.getMessage());
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = details != null ? details : Map.of();
    }
}
