package com.redhorse.deokhugam.domain.book.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;

import java.util.Map;

public class IsbnDuplicateException extends BookException
{
    public IsbnDuplicateException(String isbn) {
        super(ErrorCode.DUPLICATE_ISBN, Map.of("isbn", isbn));
    }
}
