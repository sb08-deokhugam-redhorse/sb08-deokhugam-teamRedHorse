package com.redhorse.deokhugam.domain.book.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;
import com.redhorse.deokhugam.global.exception.GlobalException;

import java.util.Map;

public class BookException extends GlobalException
{
    public BookException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
