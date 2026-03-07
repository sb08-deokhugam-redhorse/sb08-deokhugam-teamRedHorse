package com.redhorse.deokhugam.domain.user.service;

import com.redhorse.deokhugam.domain.user.dto.request.UserLoginRequest;
import com.redhorse.deokhugam.domain.user.dto.request.UserRegisterRequest;
import com.redhorse.deokhugam.domain.user.dto.request.UserUpdateRequest;
import com.redhorse.deokhugam.domain.user.dto.response.UserDto;
import java.util.UUID;

public interface UserService {

  UserDto createUser(UserRegisterRequest request);

  UserDto login(UserLoginRequest request);

  UserDto getUser(UUID userId);

  UserDto updateUser(UUID userId,UserUpdateRequest request);
}
