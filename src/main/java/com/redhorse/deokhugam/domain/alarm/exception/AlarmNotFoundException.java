package com.redhorse.deokhugam.domain.alarm.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class AlarmNotFoundException extends AlarmException {
    public AlarmNotFoundException(UUID userId) {
        super(ErrorCode.USER_NOT_FOUND, Map.of("user", userId));
    }
}