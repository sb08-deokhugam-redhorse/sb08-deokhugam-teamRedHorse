package com.redhorse.deokhugam.domain.alarm.dto;

import java.time.Instant;
import java.util.List;

public record CursorPageResponseNotificationDto(
        List<NotificationDto> content,
        String nextCursor,
        Instant nextAfter,
        int size,
        Long totalElements,
        boolean hasNext
) {
}
