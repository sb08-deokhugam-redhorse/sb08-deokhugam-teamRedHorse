package com.redhorse.deokhugam.domain.review.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReviewLikeDto(
       @NotNull UUID reviewId,
       @NotNull UUID userId,
       boolean liked
) {
}
