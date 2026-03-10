package com.redhorse.deokhugam.domain.dashboard.dto.response;

import com.redhorse.deokhugam.domain.alarm.dto.NotificationDto;
import com.redhorse.deokhugam.domain.dashboard.dto.popularbook.PopularBookDto;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularBook;

import java.time.Instant;
import java.util.List;

public record CursorPageResponsePopularBookDto(
        List<PopularBookDto> content,
        String nextCursor,
        Instant nextAfter,
        int size,
        Long totalElements,
        boolean hasNext) {
}
