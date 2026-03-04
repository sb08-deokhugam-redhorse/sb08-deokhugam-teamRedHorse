package com.redhorse.deokhugam.domain.dashboard.dto.popularbook;

import com.redhorse.deokhugam.global.entity.PeriodType;

import java.time.Instant;
import java.util.UUID;

public record PopularBookDto(
        UUID id,
        UUID bookId,
        String title,
        String thumbnailUrl,
        PeriodType period,
        long rank,
        double score,
        long reviewCount,
        double rating,
        Instant createdAt
) {
}
