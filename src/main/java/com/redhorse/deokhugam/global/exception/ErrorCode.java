package com.redhorse.deokhugam.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // user
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_DUPLICATE("이미 존재하는 사용자입니다.", HttpStatus.CONFLICT),
    LOGIN_FAILED("아이디 또는 비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),

    // review

    // book
    BOOK_NOT_FOUND("도서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_ISBN("이미 존재하는 ISBN입니다.", HttpStatus.CONFLICT);

    // comment

    // alarm
    ALARM_NOT_FOUND("알림을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);


    // dashboard

    private final String message;
    private final int status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status.value();
    }
}
