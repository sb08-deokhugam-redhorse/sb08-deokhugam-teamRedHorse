package com.redhorse.deokhugam.domain.book.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;

import java.util.Map;

public class InValidIsbnException extends BookException
{
    public InValidIsbnException(String isbn) {
        super(ErrorCode.INVALID_ISBN, Map.of("isbn", isbn == null ? "null" : isbn));
    }
}
