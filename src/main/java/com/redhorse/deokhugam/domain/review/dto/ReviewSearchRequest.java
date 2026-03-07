package com.redhorse.deokhugam.domain.review.dto;

import java.time.Instant;
import java.util.UUID;

public record ReviewSearchRequest(
    UUID userId,
    UUID bookId,
    String keyword,
    String orderBy,
    String direction,
    String cursor,
    Instant after,
    Integer limit
) {

}
