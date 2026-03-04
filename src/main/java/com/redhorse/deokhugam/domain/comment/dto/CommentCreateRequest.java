package com.redhorse.deokhugam.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record CommentCreateRequest(
    UUID reviewId,
    UUID userId,
    @NotBlank(message = "댓글 내용은 비어있을 수 없습니다.")
    String content
) {

}
