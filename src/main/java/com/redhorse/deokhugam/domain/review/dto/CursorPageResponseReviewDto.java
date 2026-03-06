package com.redhorse.deokhugam.domain.review.dto;

import java.time.Instant;
import java.util.List;

public record CursorPageResponseReviewDto(
    List<ReviewDto> content,
    String nextCursor,
    Instant nextAfter,
    int size,
    long totalElements,
    boolean hasNext
) {

}
