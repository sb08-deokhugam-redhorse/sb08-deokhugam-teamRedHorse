package com.redhorse.deokhugam.domain.user.dto.response;

import java.time.Instant;
import java.util.UUID;

public record UserDto(
    UUID id,
    String email,
    String nickname,
    Instant createdAt
) {

}
