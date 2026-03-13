package com.redhorse.deokhugam.domain.comment.dto;

import com.redhorse.deokhugam.global.exception.InvalidDirectionException;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record CommentPageRequest(
    @Schema(description = "리뷰 ID", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "리뷰 ID는 필수입니다.")
    UUID reviewId,

    @Schema(description = "정렬 방향", defaultValue = "DESC", allowableValues = {"ASC",
        "DESC"}, example = "DESC")
    String direction,

    @Schema(description = "커서 페이지네이션 커서")
    String cursor,

    @Schema(description = "보조 커서(createdAt)")
    Instant after,

    @Schema(description = "페이지 크기", defaultValue = "50", example = "50")
    @Min(value = 1, message = "limit은 1 이상이어야 합니다.")
    Integer limit
) {

  public CommentPageRequest {
    if (limit == null || limit <= 0) {
      limit = 50;
    }

    if (direction == null || direction.isBlank()) {
      direction = "DESC";
    } else {
      direction = direction.trim().toUpperCase();
      if (!direction.equals("ASC") && !direction.equals("DESC")) {
        throw new InvalidDirectionException(direction);
      }
    }
  }
}
