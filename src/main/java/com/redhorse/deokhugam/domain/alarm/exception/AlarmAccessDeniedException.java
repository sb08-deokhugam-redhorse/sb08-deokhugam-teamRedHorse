package com.redhorse.deokhugam.domain.alarm.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class AlarmAccessDeniedException extends AlarmException {
    public AlarmAccessDeniedException(UUID alarmId) {
        super(ErrorCode.ALARM_ACCES_DENIED, Map.of("alarm", alarmId));
    }
}