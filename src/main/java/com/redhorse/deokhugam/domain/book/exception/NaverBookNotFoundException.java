package com.redhorse.deokhugam.domain.book.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;

import java.util.Map;

public class NaverBookNotFoundException extends BookException
{
    public NaverBookNotFoundException(String isbn) {
        super(ErrorCode.NAVER_BOOK_NOT_FOUND, Map.of("isbn", isbn));
    }
}
