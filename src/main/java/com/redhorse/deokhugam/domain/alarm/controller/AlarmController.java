package com.redhorse.deokhugam.domain.alarm.controller;

import com.redhorse.deokhugam.domain.alarm.controller.docs.AlarmApi;
import com.redhorse.deokhugam.domain.alarm.dto.CursorPageResponseNotificationDto;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationListRequest;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationUpdateRequest;
import com.redhorse.deokhugam.domain.alarm.service.AlarmService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class AlarmController implements AlarmApi {
    private final AlarmService alarmService;

    @PatchMapping("/{notificationId}")
    public ResponseEntity<NotificationUpdateRequest> updateAlarmToRead(
            @PathVariable(value = "notificationId", required = true) UUID notificationId,
            @RequestHeader(value = "Deokhugam-Request-User-Id", required = true) UUID deokhugamRequestUserID
    ) {
        alarmService.checkAlarm(notificationId, deokhugamRequestUserID);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new NotificationUpdateRequest(true));
    }

    @PatchMapping("/read-all")
    public void updateAlarmToRead(
            @RequestHeader (value = "Deokhugam-Request-User-ID", required = true)
            @NotNull UUID deokhugamRequestUserID
    ) {
        alarmService.checkAllAlarm(deokhugamRequestUserID);
    }

    @GetMapping
    public ResponseEntity<CursorPageResponseNotificationDto> getAlarmList(@Valid NotificationListRequest request){
        CursorPageResponseNotificationDto response =  alarmService.getAlarmList(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
