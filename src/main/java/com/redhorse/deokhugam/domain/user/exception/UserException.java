package com.redhorse.deokhugam.domain.user.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;
import com.redhorse.deokhugam.global.exception.GlobalException;

import java.util.Map;

public class UserException extends GlobalException {
    public UserException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
