package com.redhorse.deokhugam.domain.alarm.controller;

import com.redhorse.deokhugam.domain.alarm.controller.docs.AlarmApi;
import com.redhorse.deokhugam.domain.alarm.dto.CursorPageResponseNotificationDto;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationListRequest;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationUpdateRequest;
import com.redhorse.deokhugam.domain.alarm.service.AlarmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class AlarmController implements AlarmApi {
    private final AlarmService alarmService;

    @PatchMapping("/{notificationId}")
    @Override
    public ResponseEntity<NotificationUpdateRequest> updateAlarmToRead(
            @PathVariable(value = "notificationId") UUID notificationId,
            @RequestHeader(value = "Deokhugam-Request-User-Id") UUID deokhugamRequestUserID
    ) {
        alarmService.checkAlarm(notificationId, deokhugamRequestUserID);
        return ResponseEntity.ok(new NotificationUpdateRequest(true));
    }

    @PatchMapping("/read-all")
    @Override
    public void updateAlarmAllToRead(
            @RequestHeader(value = "Deokhugam-Request-User-Id") UUID deokhugamRequestUserID
    ) {
        alarmService.checkAllAlarm(deokhugamRequestUserID);
    }

    @GetMapping
    @Override
    public ResponseEntity<CursorPageResponseNotificationDto> getAlarmList(NotificationListRequest request) {
        CursorPageResponseNotificationDto response = alarmService.getAlarmList(request);
        return ResponseEntity.ok(response);
    }
}
