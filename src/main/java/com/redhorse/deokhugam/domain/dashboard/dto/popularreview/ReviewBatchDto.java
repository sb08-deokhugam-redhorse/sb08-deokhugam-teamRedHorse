package com.redhorse.deokhugam.domain.dashboard.dto.popularreview;

import java.util.UUID;

public record ReviewBatchDto(
        String period,
        UUID reviewId,
        Long commentCount,
        Long likeCount,
        Double score
) {
}
