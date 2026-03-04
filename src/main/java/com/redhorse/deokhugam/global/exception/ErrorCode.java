package com.redhorse.deokhugam.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // user
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_DUPLICATE("이미 존재하는 사용자입니다.", HttpStatus.CONFLICT);

    // review

    // book

    // comment

    // alarm

    // dashboard

    private final String message;
    private final int status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status.value();
    }
}
