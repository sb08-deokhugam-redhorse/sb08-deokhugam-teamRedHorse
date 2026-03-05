package com.redhorse.deokhugam.domain.user.service.impl;

import com.redhorse.deokhugam.domain.user.dto.request.UserLoginRequest;
import com.redhorse.deokhugam.domain.user.dto.request.UserRegisterRequest;
import com.redhorse.deokhugam.domain.user.dto.response.UserDto;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.exception.UserDuplicateException;
import com.redhorse.deokhugam.domain.user.exception.UserLoginFailedException;
import com.redhorse.deokhugam.domain.user.mapper.UserMapper;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import com.redhorse.deokhugam.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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

    // 이메일 중복 검사 + 저장하는 짧은 시간 사이에
    // 누군가 저장할 수도 있으므로 try문 사용
    try {
      // DTO → Entity 변환
      User user = userMapper.toEntity(request);
      // 저장 및 반환
      userRepository.save(user);

      return userMapper.toUserDto(user);
    } catch (DataIntegrityViolationException e) {
      log.error("중복 가입 시도 감지 (동시성 요청): {}", request.email());

      throw new UserDuplicateException(request.email());
    }
  }

  @Override
  public UserDto login(UserLoginRequest request) {
    // 사용자 조회
    User user = userRepository.findByEmail(request.email())
        .orElseThrow(UserLoginFailedException::new);

    // 비번 검증 - 현재 비번 암호화는 사용 안함 / security 미사용 중
    if (!user.getPassword().equals(request.password())) {
      throw new UserLoginFailedException();
    }

    // 응답
    return userMapper.toUserDto(user);
  }
}
