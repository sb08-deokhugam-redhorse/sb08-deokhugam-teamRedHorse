package com.redhorse.deokhugam.domain.user.dto.request;

public record UserLoginRequest(
    String email,
    String password
) {

}
