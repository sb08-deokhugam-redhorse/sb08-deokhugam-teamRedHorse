package com.redhorse.deokhugam.domain.user.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.redhorse.deokhugam.domain.user.dto.request.UserRegisterRequest;
import com.redhorse.deokhugam.domain.user.dto.response.UserDto;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.exception.UserDuplicateException;
import com.redhorse.deokhugam.domain.user.mapper.UserMapper;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
            userRepository.existsUserByEmail(request.email())
        ).willReturn(false);

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
        verify(userRepository).existsUserByEmail(request.email());
        verify(userRepository).save(user);
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

        given(
            userRepository.existsUserByEmail(request.email())
        ).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.createUser(request))
            .isInstanceOf(UserDuplicateException.class);
    }
}
