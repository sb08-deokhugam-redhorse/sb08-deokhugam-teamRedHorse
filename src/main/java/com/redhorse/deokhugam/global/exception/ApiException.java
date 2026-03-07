package com.redhorse.deokhugam.global.exception;

import java.util.Map;

public class ApiException extends GlobalException
{
    public ApiException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
