package com.redhorse.deokhugam.domain.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewUpdateRequest(

    String content,

    @Min(1)
    @Max(5)
    Integer rating
) {

}
