package com.redhorse.deokhugam.domain.user.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhorse.deokhugam.domain.user.dto.request.UserLoginRequest;
import com.redhorse.deokhugam.domain.user.dto.request.UserRegisterRequest;
import com.redhorse.deokhugam.domain.user.dto.response.UserDto;
import com.redhorse.deokhugam.domain.user.exception.UserDuplicateException;
import com.redhorse.deokhugam.domain.user.exception.UserLoginFailedException;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import com.redhorse.deokhugam.domain.user.service.UserService;
import com.redhorse.deokhugam.global.config.AuthenticationInterceptor;
import com.redhorse.deokhugam.global.config.MDCLoggingInterceptor;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@DisplayName("UserController 슬라이스 테스트")
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private UserRepository userRepository;

  @MockitoBean
  private MDCLoggingInterceptor mdcLoggingInterceptor;

  @MockitoBean
  private AuthenticationInterceptor authenticationInterceptor;

  @Test
  @DisplayName("유저 회원가입 성공")
  void createUser_Success() throws Exception {
    // given
    UserRegisterRequest request = new UserRegisterRequest(
        "seongjo.park@gmail.com",
        "박성조",
        "Thisistest123***"
    );

    UserDto response = new UserDto(
        UUID.randomUUID(),
        request.email(),
        request.nickname(),
        Instant.now()
    );

    given(
        userService.createUser(eq(request))
    ).willReturn(response);

    // when
    var result = mockMvc.perform(post("/api/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    // then
    result.andExpect(status().isCreated())
        .andExpect(jsonPath("$.email").value(request.email()))
        .andExpect(jsonPath("$.nickname").value(request.nickname()));
  }

  @Test
  @DisplayName("유저 회원가입 실패 - 잘못된 이메일 형식")
  void createUser_InvalidEmail() throws Exception {
    // given
    UserRegisterRequest request = new UserRegisterRequest(
        "seongjo.park",
        "박성조",
        "Thisistest123***"
    );

    // when
    var result = mockMvc.perform(post("/api/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    // then
    result.andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("유저 회원가입 실패 - 필수값 누락")
  void createUser_BlankField() throws Exception {
    // given
    UserRegisterRequest request = new UserRegisterRequest(
        "",
        "박성조",
        "Thisistest123***"
    );

    // when
    var result = mockMvc.perform(post("/api/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    // then
    result.andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("유저 회원가입 실패 - 이메일 중복")
  void createUser_DuplicateEmail() throws Exception {
    // given
    UserRegisterRequest request = new UserRegisterRequest(
        "seongjo.park@gmail.com",
        "박성조",
        "Thisistest123***"
    );

    given(
        userService.createUser(eq(request))
    ).willThrow(new UserDuplicateException(request.email()));

    // when & then
    var result = mockMvc.perform(post("/api/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    result.andExpect(status().isConflict());
  }

  @Test
  @DisplayName("로그인 성공")
  void login_Success() throws Exception {
    // given
    UserLoginRequest request = new UserLoginRequest(
        "seongjo.park@gmail.com",
        "Thisistest123***"
    );

    UserDto response = new UserDto(
        UUID.randomUUID(),
        request.email(),
        "박성조",
        Instant.now()
    );

    given(
        userService.login(eq(request))
    ).willReturn(response);

    // when
    var result = mockMvc.perform(post("/api/users/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    // then
    result.andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(request.email()))
        .andExpect(jsonPath("$.nickname").value(response.nickname()));
  }

  @Test
  @DisplayName("로그인 실패 - 잘못된 이메일 형식")
  void login_InvalidEmail() throws Exception {
    // given
    UserLoginRequest request = new UserLoginRequest(
        "seongjo.park",
        "Thisistest123***"
    );

    // when
    var result = mockMvc.perform(post("/api/users/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    // then
    result.andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("로그인 실패 - 필수값 누락")
  void login_BlankField() throws Exception {
    // given
    UserLoginRequest request = new UserLoginRequest(
        "",
        "Thisistest123***"
    );

    // when
    var result = mockMvc.perform(post("/api/users/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    // then
    result.andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("로그인 실패 - 계정 정보 불일치")
  void login_Failed() throws Exception {
    // given
    UserLoginRequest request = new UserLoginRequest(
        "wrong@gmail.com",
        "wrongpassword"
    );

    given(
        userService.login(eq(request))
    ).willThrow(new UserLoginFailedException());

    // when
    var result = mockMvc.perform(post("/api/users/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    // then
    result.andExpect(status().isUnauthorized());
  }
}
