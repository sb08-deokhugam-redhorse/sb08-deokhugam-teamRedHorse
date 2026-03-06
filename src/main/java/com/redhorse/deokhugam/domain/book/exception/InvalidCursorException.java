package com.redhorse.deokhugam.domain.book.exception;

import com.redhorse.deokhugam.global.exception.CommonException;
import com.redhorse.deokhugam.global.exception.ErrorCode;

import java.util.Map;

public class InvalidCursorException extends CommonException
{
    public InvalidCursorException(String cursor) {
        super(ErrorCode.INVALID_CURSOR, Map.of("cursor", cursor));
    }
}
