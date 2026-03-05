package com.redhorse.deokhugam.domain.alarm.dto;

import jakarta.validation.constraints.NotNull;

public record NotificationUpdateRequest(
        boolean confirmed
) {
}
