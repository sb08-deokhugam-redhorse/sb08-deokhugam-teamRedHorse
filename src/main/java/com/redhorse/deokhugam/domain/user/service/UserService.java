package com.redhorse.deokhugam.domain.user.service;

import com.redhorse.deokhugam.domain.user.dto.request.UserRegisterRequest;
import com.redhorse.deokhugam.domain.user.dto.response.UserDto;

public interface UserService {

  UserDto createUser(UserRegisterRequest request);
}
