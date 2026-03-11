package com.redhorse.deokhugam.domain.user.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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
import com.redhorse.deokhugam.global.exception.AuthenticationException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl 단위 테스트")
class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserServiceImpl userService;

  @Test
  @DisplayName("유저 생성 성공")
  void createUser_Success() {
    // given
    UserRegisterRequest request = new UserRegisterRequest(
        "seongjo.park@gmail.com",
        "박성조",
        "Thisistest123***"
    );

    User user = new User(
        request.email(),
        request.nickname(),
        request.password()
    );

    UserDto userDto = new UserDto(
        UUID.randomUUID(),
        request.email(),
        request.nickname(),
        Instant.now()
    );

    given(
        userMapper.toEntity(request)
    ).willReturn(user);

    given(
        userMapper.toUserDto(user)
    ).willReturn(userDto);

    // when
    UserDto result = userService.createUser(request);

    // then
    assertThat(result.email()).isEqualTo(request.email());
    assertThat(result.nickname()).isEqualTo(request.nickname());
    verify(userRepository).saveAndFlush(user);
  }

  @Test
  @DisplayName("유저 생성 실패 - 이메일 중복")
  void createUser_DuplicateEmail() {
    // given
    UserRegisterRequest request = new UserRegisterRequest(
        "seongjo.park@gmail.com",
        "박성조",
        "Thisistest123***"
    );

    User user = new User(
        request.email(),
        request.nickname(),
        request.password()
    );

    given(
        userMapper.toEntity(request)
    ).willReturn(user);

    given(
        userRepository.saveAndFlush(any(User.class))
    ).willThrow(
        new DataIntegrityViolationException("이메일 중복")
    );

    // when & then
    assertThatThrownBy(() -> userService.createUser(request))
        .isInstanceOf(UserDuplicateException.class);
    verify(userRepository).saveAndFlush(any(User.class));
  }

  @Test
  @DisplayName("로그인 성공")
  void login_Success() {
    // given
    UserLoginRequest request = new UserLoginRequest(
        "seongjo.park@gmail.com",
        "Thisistest123***"
    );

    User user = new User(
        request.email(),
        "박성조",
        request.password()
    );

    UserDto userDto = new UserDto(
        UUID.randomUUID(),
        user.getEmail(),
        user.getNickname(),
        Instant.now()
    );

    given(
        userRepository.findByEmail(request.email())
    ).willReturn(Optional.of(user));

    given(
        userMapper.toUserDto(user)
    ).willReturn(userDto);

    // when
    UserDto result = userService.login(request);

    // then
    assertThat(result.email()).isEqualTo(request.email());
    assertThat(result.nickname()).isEqualTo(user.getNickname());
    verify(userRepository).findByEmail(request.email());
  }

  @Test
  @DisplayName("로그인 실패 - 비밀번호 불일치")
  void login_WrongPassword() {
    // given
    UserLoginRequest request = new UserLoginRequest(
        "seongjo.park@gmail.com",
        "Thisistest123***"
    );

    User user = new User(
        request.email(),
        "박성조",
        "THIS_IS_WRONG_123"
    );

    given(
        userRepository.findByEmail(request.email())
    ).willReturn(Optional.of(user));

    // when & then
    assertThatThrownBy(() -> userService.login(request))
        .isInstanceOf(UserLoginFailedException.class);
  }

  @Test
  @DisplayName("로그인 실패 - 존재하지 않는 유저")
  void login_UserNotFound() {
    // given
    UserLoginRequest request = new UserLoginRequest(
        "seongjo.park@gmail.com",
        "Thisistest123***"
    );

    given(
        userRepository.findByEmail(request.email())
    ).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userService.login(request))
        .isInstanceOf(UserLoginFailedException.class);
  }

  @Test
  @DisplayName("사용자 조회 성공")
  void read_user() {
    // given
    UUID userId = UUID.randomUUID();
    User user = new User(
        "seongjo.park@gmail.com",
        "박성조",
        "Thisistest123***"
    );
    UserDto expectedDto = new UserDto(
        userId,
        user.getEmail(),
        user.getNickname(),
        Instant.now()
    );

    given(
        userRepository.findById(userId)
    ).willReturn(Optional.of(user));

    given(
        userMapper.toUserDto(user)
    ).willReturn(expectedDto);

    // when & Then
    UserDto result = userService.getUser(userId);

    assertThat(result.email()).isEqualTo(expectedDto.email());
    assertThat(result.nickname()).isEqualTo(expectedDto.nickname());

    verify(userRepository).findById(userId);
  }

  @Test
  @DisplayName("사용자 조회 실패 - 없는 사용자 ID")
  void read_user_failed() {
    // given
    UUID userId = UUID.randomUUID();

    given(
        userRepository.findById(userId)
    ).willReturn(Optional.empty());

    // when & Then
    assertThatThrownBy(() -> userService.getUser(userId))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("사용자 업데이트 성공 - 닉네임만 수정 가능")
  void update_user() {
    // given
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("박성조-수정");

    User user = new User(
        "seongjo.park@gmail.com",
        "박성조",
        "Thisistest123***"
    );

    // ID임시 주입
    ReflectionTestUtils.setField(user, "id", userId);

    given(
        userRepository.findById(userId)
    ).willReturn(Optional.of(user));


    UserDto expectedDto = new UserDto(
        userId,
        user.getEmail(),
        request.nickname(),
        Instant.now()
    );

    given(
        userMapper.toUserDto(user)
    ).willReturn(expectedDto);

    // When & Then
    UserDto result = userService.updateUser(userId, userId, request);

    assertThat(user.getNickname()).isEqualTo(request.nickname());
    assertThat(result.nickname()).isEqualTo(request.nickname());
  }

  @Test
  @DisplayName("사용자 SOFT 삭제 성공")
  void softDelete_user() {
    // given
    UUID userId = UUID.randomUUID();
    User user = new User(
        "seongjo.park@gmail.com",
        "박성조",
        "Thisistest123***"
    );

    // ID임시 주입
    ReflectionTestUtils.setField(user, "id", userId);

    given(
        userRepository.findById(userId)
    ).willReturn(Optional.of(user));


    // When & Then
    userService.deleteUserSoft(userId, userId);


    assertThat(user.isDeleted()).isEqualTo(true);
    assertThat(user.getDeletedAt()).isNotNull();
  }

  @Test
  @DisplayName("사용자 HARD 삭제 성공")
  void hardDelete_user_Success() {
    // given
    UUID userId = UUID.randomUUID();
    User user = new User(
        "seongjo.park@gmail.com",
        "박성조",
        "Thisistest123***"
    );

    // 물리 삭제 조건 설정: ID 주입, Soft Delete 상태(true), 삭제 시간(25시간 전)
    ReflectionTestUtils.setField(user, "id", userId);
    ReflectionTestUtils.setField(user, "isDeleted", true);
    ReflectionTestUtils.setField(user, "deletedAt", Instant.now().minus(java.time.Duration.ofHours(25)));

    given(
        userRepository.findByIdIncludeDeleted(userId)
    ).willReturn(Optional.of(user));

    given(
        userRepository.deleteHardById(userId)
    ).willReturn(1);

    // when
    userService.deleteUserHard(userId, userId);

    // then
    verify(userRepository).findByIdIncludeDeleted(userId);
    verify(userRepository).deleteHardById(userId);
  }

  @Test
  @DisplayName("사용자 HARD 삭제 실패 - 존재하지 않는 사용자")
  void hardDelete_user_NotFound() {
    // given
    UUID userId = UUID.randomUUID();

    given(
        userRepository.findByIdIncludeDeleted(userId)
    ).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userService.deleteUserHard(userId, userId))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("사용자 HARD 삭제 실패 - 권한 없음")
  void hardDelete_user_AuthenticationFailed() {
    // given
    UUID userId = UUID.randomUUID();
    UUID anotherUserId = UUID.randomUUID();
    User user = new User(
        "seongjo.park@gmail.com",
        "박성조",
        "Thisistest123***"
    );

    ReflectionTestUtils.setField(user, "id", userId);

    given(
        userRepository.findByIdIncludeDeleted(userId)
    ).willReturn(Optional.of(user));

    // when & then
    assertThatThrownBy(() -> userService.deleteUserHard(anotherUserId, userId))
        .isInstanceOf(AuthenticationException.class);
  }

  @Test
  @DisplayName("사용자 HARD 삭제 실패 - 소프트 삭제되지 않은 유저")
  void hardDelete_user_NotSoftDeleted() {
    // given
    UUID userId = UUID.randomUUID();
    User user = new User(
        "seongjo.park@gmail.com",
        "박성조",
        "Thisistest123***"
    );

    ReflectionTestUtils.setField(user, "id", userId);
    ReflectionTestUtils.setField(user, "isDeleted", false);

    given(
        userRepository.findByIdIncludeDeleted(userId)
    ).willReturn(Optional.of(user));

    // when & then
    assertThatThrownBy(() -> userService.deleteUserHard(userId, userId))
        .isInstanceOf(UserNotSoftDeletedException.class);
  }

  @Test
  @DisplayName("사용자 HARD 삭제 실패 - 유예 기간(1일) 미경과")
  void hardDelete_user_NotYet() {
    // given
    UUID userId = UUID.randomUUID();
    User user = new User(
        "seongjo.park@gmail.com",
        "박성조",
        "Thisistest123***"
    );

    ReflectionTestUtils.setField(user, "id", userId);
    ReflectionTestUtils.setField(user, "isDeleted", true);
    ReflectionTestUtils.setField(user, "deletedAt", Instant.now());

    given(
        userRepository.findByIdIncludeDeleted(userId)
    ).willReturn(Optional.of(user));

    // when & then
    assertThatThrownBy(() -> userService.deleteUserHard(userId, userId))
        .isInstanceOf(UserDeletedNotYetException.class);
  }
}
