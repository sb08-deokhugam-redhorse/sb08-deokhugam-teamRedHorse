package com.redhorse.deokhugam.domain.dashboard.dto.response;

import com.redhorse.deokhugam.domain.dashboard.dto.poweruser.PowerUserDto;

import java.time.Instant;
import java.util.List;

public record CursorPageResponsePowerUserDto(
        List<PowerUserDto> content,
        String nextCursor,
        Instant nextAfter,
        int size,
        Long totalElements,
        boolean hasNext) {
}
