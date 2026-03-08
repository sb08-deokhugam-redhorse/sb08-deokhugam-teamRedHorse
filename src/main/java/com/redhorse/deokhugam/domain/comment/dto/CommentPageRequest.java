package com.redhorse.deokhugam.domain.comment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record CommentPageRequest(
    @NotNull(message = "리뷰 ID는 필수입니다.")
    UUID reviewId,
    String direction,
    String cursor,
    Instant after,
    @Min(value = 1, message = "limit은 1 이상이어야 합니다.")
    Integer limit
) {

}
