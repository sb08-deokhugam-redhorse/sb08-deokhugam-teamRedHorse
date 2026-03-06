package com.redhorse.deokhugam.global.exception;

import java.util.Map;

public class InvalidCursorException extends CommonException
{
    public InvalidCursorException(String cursor) {
        super(ErrorCode.INVALID_CURSOR, Map.of("cursor", cursor));
    }
}
