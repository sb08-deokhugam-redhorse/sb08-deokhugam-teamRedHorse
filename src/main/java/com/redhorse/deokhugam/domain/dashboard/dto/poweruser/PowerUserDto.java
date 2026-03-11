package com.redhorse.deokhugam.domain.dashboard.dto.poweruser;

import com.redhorse.deokhugam.global.entity.PeriodType;

import java.time.Instant;
import java.util.UUID;

public record PowerUserDto(
        UUID userId,
        String nickname,
        PeriodType period,
        Instant createdAt,
        Long rank,
        double score,
        double reviewScoreSum,
        Long likeCount,
        Long commentCount
) {
}
