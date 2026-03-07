package com.redhorse.deokhugam.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @NotBlank(message = "닉네임 필수 입력값입니다.")
    @Size(min = 2, max = 20, message = "닉네임 2 ~ 20자 이내여야 합니다.")
    String nickname
) {

}
