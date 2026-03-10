package com.redhorse.deokhugam.domain.user.service.impl;

import com.redhorse.deokhugam.domain.user.dto.request.UserLoginRequest;
import com.redhorse.deokhugam.domain.user.dto.request.UserRegisterRequest;
import com.redhorse.deokhugam.domain.user.dto.request.UserUpdateRequest;
import com.redhorse.deokhugam.domain.user.dto.response.UserDto;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.exception.UserDeletedNotYetException;
import com.redhorse.deokhugam.domain.user.exception.UserDuplicateException;
import com.redhorse.deokhugam.domain.user.exception.UserLoginFailedException;
import com.redhorse.deokhugam.domain.user.exception.UserNotFoundException;
import com.redhorse.deokhugam.domain.user.exception.UserNotSoftDeletedException;
import com.redhorse.deokhugam.domain.user.mapper.UserMapper;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import com.redhorse.deokhugam.domain.user.service.UserService;
import com.redhorse.deokhugam.global.exception.AuthenticationException;
import java.time.Duration;
import java.time.Instant;
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

  @Override
  @Transactional
  public UserDto updateUser(UUID userId, UUID requestUserId, UserUpdateRequest request) {

    // 유저 체크
    User findUser = userRepository.findById(userId)
        .orElseThrow(()-> new UserNotFoundException(userId));

    // 헤더의 ID와 비교
    if (!findUser.getId().equals(requestUserId)) {
      throw new AuthenticationException();
    }

    // 변경하려는 닉네임이 기존과 다를 경우에만 수정
    if (!findUser.getNickname().equals(request.nickname())) {
      findUser.updateNickname(request.nickname());
    }

    log.info("[User-Service] 사용자 정보 수정 완료: userId = {}, nickname = {}", userId, request.nickname());

    // @Transactional로 인해 명시적인 repository.save()불필요. 바로 리턴
    return userMapper.toUserDto(findUser);
  }

  @Override
  @Transactional
  public void deleteUserSoft(UUID requestUserId ,UUID userId) {
    // 유저 체크
    User findUser = userRepository.findById(userId)
        .orElseThrow(()-> new UserNotFoundException(userId));

    // 헤더의 ID와 비교
    if (!findUser.getId().equals(requestUserId)) {
      throw new AuthenticationException();
    }

    findUser.softDelete();
  }

  @Override
  @Transactional
  public void deleteUserHard(UUID requestUserId, UUID userId) {
    // 유저 체크 - Soft Delete 여부와 상관없이 유저 조회 (Native Query 사용)
    User findUser = userRepository.findByIdIncludeDeleted(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

    // 헤더의 ID와 비교
    if (!findUser.getId().equals(requestUserId)) {
      throw new AuthenticationException();
    }

    // 소프트 삭제 여부 확인
    if (!findUser.isDeleted()) {
        log.warn("[User-Service] 물리 삭제 실패: 소프트 삭제되지 않은 유저. userId = {}", userId);
        throw new UserNotSoftDeletedException();
    }

    // 유예 기간 확인 (현재 - 1일 이전이어야 함)
    Instant oneDayAgo = Instant.now().minus(Duration.ofDays(1));

    if (findUser.getDeletedAt().isAfter(oneDayAgo)) {
        log.warn("[User-Service] 물리 삭제 실패: 유예 기간(1일) 미경과. userId = {}, 삭제일시 = {}", 
                 userId, findUser.getDeletedAt());
        throw new UserDeletedNotYetException();
    }

    // 삭제 처리
    int deletedCount = userRepository.deleteHardById(findUser.getId());

    if (deletedCount == 0) {
      // 이미 다른 경로(배치 등)에서 삭제된 경우
      log.warn("[User-Service] 물리 삭제 대상 없음(이미 삭제됨): userId = {}", userId);

      return;
    }

    log.info("[User-Service] 사용자 물리 삭제 완료: userId = {}", userId);
  }
}
