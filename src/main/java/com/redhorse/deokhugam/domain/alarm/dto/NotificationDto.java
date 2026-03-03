package com.redhorse.deokhugam.domain.alarm.dto;

import com.redhorse.deokhugam.global.entity.PeriodType;

import java.time.Instant;
import java.util.UUID;

public record NotificationDto(
        UUID id,
        UUID userId,
        UUID reviewId,
        PeriodType reviewContent, // 원래는 title
        String message, // 원래는 content
        boolean confirmed,
        Instant createdAt,
        Instant updatedAt
) {
}
