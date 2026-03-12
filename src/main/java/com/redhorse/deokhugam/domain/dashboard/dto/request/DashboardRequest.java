package com.redhorse.deokhugam.domain.dashboard.dto.request;

import com.redhorse.deokhugam.global.entity.PeriodType;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record DashboardRequest(
        @NotNull PeriodType period,
        String direction,
//        String cursor,
        UUID cursor,
        Instant after,
        Integer limit
) {
}
