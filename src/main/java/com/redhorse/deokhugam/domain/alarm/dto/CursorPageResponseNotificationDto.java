package com.redhorse.deokhugam.domain.alarm.dto;

import java.time.Instant;
import java.util.List;

public record CursorPageResponseNotificationDto(
        List<NotificationDto> contents,
        Instant nextCursor,
        Instant nextAfter,
        int size,
        int totalElements,
        boolean hasNext
) {
}
