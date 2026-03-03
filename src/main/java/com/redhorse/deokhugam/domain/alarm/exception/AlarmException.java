package com.redhorse.deokhugam.domain.alarm.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;
import com.redhorse.deokhugam.global.exception.GlobalException;

import java.util.Map;

public class AlarmException extends GlobalException {
    public AlarmException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
