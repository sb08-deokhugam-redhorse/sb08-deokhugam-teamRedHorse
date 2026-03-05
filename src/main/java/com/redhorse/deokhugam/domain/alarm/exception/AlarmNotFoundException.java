package com.redhorse.deokhugam.domain.alarm.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class AlarmNotFoundException extends AlarmException {
    public AlarmNotFoundException(UUID alarmId) {
        super(ErrorCode.ALARM_NOT_FOUND, Map.of("alarm", alarmId));
    }
}