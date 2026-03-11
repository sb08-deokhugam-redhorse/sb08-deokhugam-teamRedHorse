package com.redhorse.deokhugam.domain.dashboard.dto.response;

import com.redhorse.deokhugam.domain.dashboard.dto.popularreview.PopularReviewDto;

import java.time.Instant;
import java.util.List;

public record CursorPageResponsePopularReviewkDto(
        List<PopularReviewDto> content,
        String nextCursor,
        Instant nextAfter,
        int size,
        Long totalElements,
        boolean hasNext) {
}
