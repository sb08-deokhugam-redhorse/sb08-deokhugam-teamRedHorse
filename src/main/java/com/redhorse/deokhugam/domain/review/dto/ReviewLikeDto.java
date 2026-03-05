package com.redhorse.deokhugam.domain.review.dto;

import java.util.UUID;

public record ReviewLikeDto(
        UUID reviewId,
        UUID userId,
        boolean like
) {
}
