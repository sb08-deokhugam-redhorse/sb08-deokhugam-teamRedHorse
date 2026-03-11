package com.redhorse.deokhugam.domain.dashboard.dto.popularbook;

import java.util.UUID;

public record BookBatchDto(
        String period,
        UUID bookId,
        Long reviewCount,
        Double rating,
        Double score
) {
}
