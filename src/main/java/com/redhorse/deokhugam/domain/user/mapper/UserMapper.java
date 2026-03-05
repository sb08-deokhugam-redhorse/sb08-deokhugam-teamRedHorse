package com.redhorse.deokhugam.domain.user.mapper;

import com.redhorse.deokhugam.domain.user.dto.request.UserRegisterRequest;
import com.redhorse.deokhugam.domain.user.dto.response.UserDto;
import com.redhorse.deokhugam.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  // 유저 생성 DTO → Entity
  @Mapping(target = "comments", ignore = true)
  @Mapping(target = "reviews", ignore = true)
  @Mapping(target = "reviewLikes", ignore = true)
  @Mapping(target = "alarms", ignore = true)
  @Mapping(target = "powerUsers", ignore = true)
  User toEntity(UserRegisterRequest request);

  // 유저 Entity → 유저 조회 DTO
  UserDto toUserDto(User user);
}
