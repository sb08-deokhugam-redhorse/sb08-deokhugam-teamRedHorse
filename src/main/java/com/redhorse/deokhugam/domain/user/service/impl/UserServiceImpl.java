package com.redhorse.deokhugam.domain.user.service.impl;

import com.redhorse.deokhugam.domain.user.dto.request.UserRegisterRequest;
import com.redhorse.deokhugam.domain.user.dto.response.UserDto;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.exception.UserDuplicateException;
import com.redhorse.deokhugam.domain.user.mapper.UserMapper;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import com.redhorse.deokhugam.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public UserDto createUser(UserRegisterRequest request) {
    String email = request.email();

    // 이메일 중복검사
    if (userRepository.existsUserByEmail(email)) {
      throw new UserDuplicateException(email);
    }

    // DTO → Entity 변환
    User user = userMapper.toEntity(request);

    // 저장 및 반환
    userRepository.save(user);

    return userMapper.toUserDto(user);
  }
}
