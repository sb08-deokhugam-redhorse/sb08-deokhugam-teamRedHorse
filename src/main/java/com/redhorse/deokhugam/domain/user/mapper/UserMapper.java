package com.redhorse.deokhugam.domain.user.mapper;

import com.redhorse.deokhugam.domain.user.dto.request.UserRegisterRequest;
import com.redhorse.deokhugam.domain.user.dto.response.UserDto;
import com.redhorse.deokhugam.domain.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

  // 유저 생성 DTO → Entity
  User toEntity(UserRegisterRequest request);

  // 유저 Entity → 유저 조회 DTO
  UserDto toUserDto(User user);
}
