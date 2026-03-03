package com.redhorse.deokhugam.domain.alarm.service;

import com.redhorse.deokhugam.domain.alarm.dto.NotificationDto;

public interface AlarmService {
    NotificationDto createDashboardAlarm();
    NotificationDto createCommentAlarm();
    void checkAlarm();
    void checkAllAlarm();
    void deleteAlarm();
    void getAlarmList();
}
