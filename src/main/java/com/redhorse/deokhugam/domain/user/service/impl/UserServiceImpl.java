package com.redhorse.deokhugam.domain.user.service.impl;

import com.redhorse.deokhugam.domain.user.dto.request.UserLoginRequest;
import com.redhorse.deokhugam.domain.user.dto.request.UserRegisterRequest;
import com.redhorse.deokhugam.domain.user.dto.response.UserDto;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.exception.UserDuplicateException;
import com.redhorse.deokhugam.domain.user.exception.UserLoginFailedException;
import com.redhorse.deokhugam.domain.user.exception.UserNotFoundException;
import com.redhorse.deokhugam.domain.user.mapper.UserMapper;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import com.redhorse.deokhugam.domain.user.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public UserDto createUser(UserRegisterRequest request) {
    // DB에 email Unique 속성이 있기때문에
    // 이메일 중복검사 안하고 바로 저장 시켜서 확인
    try {
      // DTO → Entity 변환
      User user = userMapper.toEntity(request);

      // 저장 및 반환
      userRepository.saveAndFlush(user);

      log.info("[User-Service] 작업 완료");
      return userMapper.toUserDto(user);
    } catch (DataIntegrityViolationException e) {

      log.error("[User-Service] 에러 중복 이메일: detail = {}", request.email());
      throw new UserDuplicateException(request.email());
    }
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto login(UserLoginRequest request) {
    // 사용자 조회
    User user = userRepository.findByEmail(request.email())
        .orElseThrow(() -> {
          log.error("[User-Service] 에러 없는 이메일: detail = {}", request.email());
          return new UserLoginFailedException();
        });

    // 비번 검증 - 현재 비번 암호화는 사용 안함 / security 미사용 중
    if (!user.getPassword().equals(request.password())) {
      log.error("[User-Service] 에러 비밀번호 다름: detail = 비밀번호 다름");
      throw new UserLoginFailedException();
    }

    UserDto result = userMapper.toUserDto(user);

    // 응답
    log.info("[User-Service] 작업 완료: content {}", result.id());
    return result;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto getUser(UUID userId) {
    // 사용자 조회
    User user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException(userId));

    UserDto result = userMapper.toUserDto(user);

    // 응답
    log.info("[User-Service] 작업 완료: content {}", result.id());
    return result;
  }
}
