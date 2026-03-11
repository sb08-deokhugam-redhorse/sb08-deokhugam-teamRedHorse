package com.redhorse.deokhugam.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // user
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_DUPLICATE("이미 존재하는 사용자입니다.", HttpStatus.CONFLICT),
    LOGIN_FAILED("아이디 또는 비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED_USER("해당 요청에 대한 권한이 없습니다.", HttpStatus.UNAUTHORIZED),
    USER_NOT_SOFT_DELETED("탈퇴 처리되지 않은 사용자입니다.", HttpStatus.BAD_REQUEST),
    HARD_DELETE_NOT_ALLOWED_YET("탈퇴 후 24시간이 지나야 영구 삭제가 가능합니다.", HttpStatus.BAD_REQUEST),

    // review
    REVIEW_NOT_FOUND("리뷰를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    BOOKID_USERID_EXISTS("이미 해당 책에 리뷰를 작성했습니다.",HttpStatus.CONFLICT),
    ONLY_THE_REVIEW_AUTHOR("리뷰 작성자만 수정/삭제할 수 있습니다.",HttpStatus.FORBIDDEN),
    REVIEW_VALIDATION("내용과 별점을 작성해야 합니다.", HttpStatus.BAD_REQUEST),


    // book
    BOOK_NOT_FOUND("도서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_ISBN("이미 존재하는 ISBN입니다.", HttpStatus.CONFLICT),
    INVALID_ISBN("ISBN이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),

    // comment
    COMMENT_NOT_FOUND("댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    COMMENT_UPDATE_NOT_ALLOWED("자신이 작성한 댓글만 수정할 수 있습니다.", HttpStatus.FORBIDDEN),
    COMMENT_DELETE_NOT_ALLOWED("자신이 작성한 댓글만 삭제할 수 있습니다.", HttpStatus.FORBIDDEN),

    // alarm
    ALARM_NOT_FOUND("알림을 찾을 수 없습니다.",HttpStatus.NOT_FOUND),


    // dashboard

    // common
    INVALID_CURSOR("유효하지 않은 cursor 값 입니다.", HttpStatus.BAD_REQUEST),

    // infra
    ISBN_NOT_FOUND("이미지에서 ISBN을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    NAVER_BOOK_NOT_FOUND("네이버에서 도서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NAVER_API_ERROR("네이버 API 호출 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_SIZE_EXCEEDED("이미지 크기가 1MB를 초과합니다.", HttpStatus.BAD_REQUEST),
    OCR_PROCESSING_FAILED("OCR 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    S3_UPLOAD_FAIL("S3 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final int status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status.value();
    }
}
