package com.redhorse.deokhugam.domain.book.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;
import com.redhorse.deokhugam.global.exception.GlobalException;

import java.util.Map;

public class IsbnException extends GlobalException
{
    public IsbnException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
