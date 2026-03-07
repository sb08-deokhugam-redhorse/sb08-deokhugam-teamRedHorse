package com.redhorse.deokhugam.domain.alarm.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record NotificationListRequest(
        @NotNull UUID userId,
        String direction,
        String cursor,
        Instant after, //createdAt
        int limit
) {
    public NotificationListRequest {
        if (direction == null || direction.isBlank()) {
            direction = "DESC";
        }

        if (limit <= 0) {
            limit = 20;
        } else if (limit > 100) {
            limit = 100;
        }
    }
}
