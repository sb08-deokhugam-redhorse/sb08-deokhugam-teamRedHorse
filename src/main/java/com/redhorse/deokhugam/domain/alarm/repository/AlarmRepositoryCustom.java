package com.redhorse.deokhugam.domain.alarm.repository;

import com.redhorse.deokhugam.domain.alarm.dto.NotificationListRequest;
import com.redhorse.deokhugam.domain.alarm.entity.Alarm;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface AlarmRepositoryCustom {
    Slice<Alarm> getAllAlarms(NotificationListRequest request, Pageable pageable);
}