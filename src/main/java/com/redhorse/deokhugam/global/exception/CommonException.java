package com.redhorse.deokhugam.global.exception;

import java.util.Map;

public class CommonException extends GlobalException
{
    public CommonException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
