package com.redhorse.deokhugam.domain.review.dto;

import java.util.List;

public record CursorPageResponseReviewDto(
    List<ReviewDto> content,
    String nextCursor,
    String nextAfter,
    int size,
    int totalElements,
    boolean hasNext
) {

}
