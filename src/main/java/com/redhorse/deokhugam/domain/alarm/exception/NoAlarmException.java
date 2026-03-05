package com.redhorse.deokhugam.domain.alarm.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class NoAlarmException extends AlarmException {
    public NoAlarmException(UUID userId) {
        super(ErrorCode.ALARM_NOT_FOUND, Map.of("alarm", userId + " alarm empty"));
    }
}