package com.redhorse.deokhugam.domain.alarm.dto;

import java.util.ArrayList;

public record CursorPageResponseNotificationDto(
        ArrayList<Object> content,
        String nextCursor,
        String nextAfter, // 스웨그 보면 date-time도 있는데 Instant로 해야 하나?
        int size,
        int totalElements,
        boolean hasNext
) {
}
