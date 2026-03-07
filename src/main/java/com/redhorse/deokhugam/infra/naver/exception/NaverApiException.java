package com.redhorse.deokhugam.infra.naver.exception;

import com.redhorse.deokhugam.domain.book.exception.BookException;
import com.redhorse.deokhugam.global.exception.ErrorCode;

import java.util.Map;

public class NaverApiException extends BookException
{
    public NaverApiException(String isbn) {
        super(ErrorCode.NAVER_API_ERROR, Map.of("isbn: ", isbn));
    }
}
