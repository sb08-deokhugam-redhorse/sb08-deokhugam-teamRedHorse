package com.redhorse.deokhugam.domain.dashboard.dto.poweruser;

import java.util.UUID;

public record UserBatchDto(
        String period,
        UUID userId,
        Long likeCount,
        Long commentCount,
        Double score,
        Double reviewScoreSum
) {
}
