package com.redhorse.deokhugam.domain.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewUpdateRequest(

    @NotBlank(message = "수정 내용은 필수입니다.")
    String content,

    @Min(1)
    @Max(5)
    @NotNull(message = "평점은 필수입니다.")
    Integer rating
) {

}
