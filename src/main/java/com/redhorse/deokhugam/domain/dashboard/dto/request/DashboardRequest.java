package com.redhorse.deokhugam.domain.dashboard.dto.request;

import com.redhorse.deokhugam.global.entity.PeriodType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.Instant;
import java.util.UUID;

public record DashboardRequest(
        @NotNull PeriodType period,
        @NotNull
        @Pattern(regexp = "^(ASC|DESC)$",
                flags = Pattern.Flag.CASE_INSENSITIVE,
                message = "정렬 방향은 ASC 또는 DESC만 허용됩니다.")
        String direction,
        UUID cursor,
        Instant after,
        @Min(value = 1, message = "limit은 1 이상이어야 합니다.") Integer limit
) {
}
