package com.redhorse.deokhugam.domain.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReviewCreateRequest(

    @NotNull(message = "책 ID는 필수입니다.")
    UUID bookId,

    @NotNull(message = "유저 ID는 필수입니다.")
    UUID userId,

    @NotBlank(message = "내용은 필수입니다.")
    String content,

    @Min(1)
    @Max(5)
    @NotNull(message = "평점은 필수입니다.")
    int rating
) {

}
