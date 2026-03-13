package com.redhorse.deokhugam.domain.dashboard.dto.request;

import com.redhorse.deokhugam.global.entity.PeriodType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.Instant;
import java.util.UUID;

public record DashboardRequest(
        @NotNull PeriodType period,
        @Pattern(regexp = "^(ASC|DESC)$", flags = Pattern.Flag.CASE_INSENSITIVE) String direction,
        UUID cursor,
        Instant after,
        @Min(1) Integer limit
) {
}
