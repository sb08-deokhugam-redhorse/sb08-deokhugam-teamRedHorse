package com.redhorse.deokhugam.domain.user.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhorse.deokhugam.domain.user.dto.request.UserLoginRequest;
import com.redhorse.deokhugam.domain.user.dto.request.UserRegisterRequest;
import com.redhorse.deokhugam.domain.user.dto.request.UserUpdateRequest;
import com.redhorse.deokhugam.domain.user.dto.response.UserDto;
import com.redhorse.deokhugam.domain.user.exception.UserDeletedNotYetException;
import com.redhorse.deokhugam.domain.user.exception.UserDuplicateException;
import com.redhorse.deokhugam.domain.user.exception.UserLoginFailedException;
import com.redhorse.deokhugam.domain.user.exception.UserNotFoundException;
import com.redhorse.deokhugam.domain.user.exception.UserNotSoftDeletedException;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import com.redhorse.deokhugam.domain.user.service.UserService;
import com.redhorse.deokhugam.global.exception.AccessDeniedException;
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

  @Test
  @DisplayName("사용자 조회 성공")
  void read_user() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    UserDto response = new UserDto(
        userId,
        "seongjo.park@gmail.com",
        "박성조",
        Instant.now()
    );

    given(
        userService.getUser(userId)
    ).willReturn(response);

    // when
    var result = mockMvc.perform(get("/api/users/{userId}", userId)
        .header("Deokhugam-Request-User-ID", userId)
        .contentType(MediaType.APPLICATION_JSON));

    // then
    result.andExpect(status().isOk());
    result.andExpect(jsonPath("$.nickname").value(response.nickname()));
    result.andExpect(jsonPath("$.email").value(response.email()));
  }

  @Test
  @DisplayName("사용자 조회 실패 - 존재하지 않는 사용자의 ID")
  void read_user_failed() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    given(
        userService.getUser(userId)
    ).willThrow(new UserNotFoundException(userId));

    // when
    var result = mockMvc.perform(get("/api/users/{userId}", userId)
        .header("Deokhugam-Request-User-ID", userId)
        .contentType(MediaType.APPLICATION_JSON));

    // then
    result.andExpect(status().isNotFound());
    result.andExpect(jsonPath("$.message").value("사용자를 찾을 수 없습니다."));
  }

  @Test
  @DisplayName("사용자 수정 성공")
  void update_user() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    UserUpdateRequest request = new UserUpdateRequest("박성조-수정");

    UserDto response = new UserDto(
        userId,
        "seongjo.park@gmail.com",
        "박성조-수정",
        Instant.now()
    );

    given(
        userService.updateUser(
            eq(userId),
            eq(userId),
            eq(request))
    ).willReturn(response);

    // when
    var result = mockMvc.perform(patch("/api/users/{userId}", userId)
        .header("Deokhugam-Request-User-ID", userId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    // then
    result.andExpect(status().isOk());
    result.andExpect(jsonPath("$.nickname").value(response.nickname()));
  }

  @Test
  @DisplayName("사용자 SOFT 삭제 성공")
  void soft_delete_user() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    willDoNothing()
        .given(userService)
        .deleteUserSoft(eq(userId), eq(userId));

    // when
    var result = mockMvc.perform(delete("/api/users/{userId}", userId)
        .header("Deokhugam-Request-User-ID", userId));

    // then
    result.andExpect(status().isNoContent());
    verify(userService).deleteUserSoft(eq(userId), eq(userId));
  }

  @Test
  @DisplayName("사용자 SOFT 삭제 실패 - 존재하지 않는 사용자의 ID")
  void soft_delete_user_NotFound() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    willThrow(new UserNotFoundException(userId))
        .given(userService)
        .deleteUserSoft(eq(userId), eq(userId));

    // when
    var result = mockMvc.perform(delete("/api/users/{userId}", userId)
        .header("Deokhugam-Request-User-ID", userId));

    // then
    result.andExpect(status().isNotFound());
    result.andExpect(jsonPath("$.message").value("사용자를 찾을 수 없습니다."));
  }

  @Test
  @DisplayName("사용자 SOFT 삭제 실패 - 권한 없음 (ID 불일치)")
  void soft_delete_user_isForbidden() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    UUID anotherUserId = UUID.randomUUID();

    willThrow(new AccessDeniedException())
        .given(userService)
        .deleteUserSoft(eq(anotherUserId), eq(userId));

    // when
    var result = mockMvc.perform(delete("/api/users/{userId}", userId)
        .header("Deokhugam-Request-User-ID", anotherUserId));

    // then
    result.andExpect(status().isForbidden());
    result.andExpect(jsonPath("$.message").value("해당 요청에 대한 권한이 없습니다."));
  }

  @Test
  @DisplayName("사용자 SOFT 삭제 실패 - 필수 헤더 누락")
  void soft_delete_user_MissingHeader() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    // when
    var result = mockMvc.perform(delete("/api/users/{userId}", userId));

    // then
    result.andExpect(status().isUnauthorized());
    result.andExpect(jsonPath("$.message").value("인증에 실패하였습니다."));
  }

  @Test
  @DisplayName("사용자 HARD 삭제 성공")
  void hard_delete_user() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    willDoNothing()
        .given(userService)
        .deleteUserHard(eq(userId), eq(userId));

    // when
    var result = mockMvc.perform(delete("/api/users/{userId}/hard", userId)
        .header("Deokhugam-Request-User-ID", userId));

    // then
    result.andExpect(status().isNoContent());
    verify(userService).deleteUserHard(eq(userId), eq(userId));
  }

  @Test
  @DisplayName("사용자 HARD 삭제 실패 - 존재하지 않는 사용자의 ID")
  void hard_delete_user_NotFound() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    willThrow(new UserNotFoundException(userId))
        .given(userService)
        .deleteUserHard(eq(userId), eq(userId));

    // when
    var result = mockMvc.perform(delete("/api/users/{userId}/hard", userId)
        .header("Deokhugam-Request-User-ID", userId));

    // then
    result.andExpect(status().isNotFound());
    result.andExpect(jsonPath("$.message").value("사용자를 찾을 수 없습니다."));
  }

  @Test
  @DisplayName("사용자 HARD 삭제 실패 - 권한 없음 (ID 불일치)")
  void hard_delete_user_isForbidden() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    UUID anotherUserId = UUID.randomUUID();

    willThrow(new AccessDeniedException())
        .given(userService)
        .deleteUserHard(eq(anotherUserId), eq(userId));

    // when
    var result = mockMvc.perform(delete("/api/users/{userId}/hard", userId)
        .header("Deokhugam-Request-User-ID", anotherUserId));

    // then
    result.andExpect(status().isForbidden());
    result.andExpect(jsonPath("$.message").value("해당 요청에 대한 권한이 없습니다."));
  }

  @Test
  @DisplayName("사용자 HARD 삭제 실패 - 소프트 삭제되지 않은 사용자")
  void hard_delete_user_NotSoftDeleted() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    willThrow(new UserNotSoftDeletedException())
        .given(userService)
        .deleteUserHard(eq(userId), eq(userId));

    // when
    var result = mockMvc.perform(delete("/api/users/{userId}/hard", userId)
        .header("Deokhugam-Request-User-ID", userId));

    // then
    result.andExpect(status().isBadRequest());
    result.andExpect(jsonPath("$.message").value("탈퇴 처리되지 않은 사용자입니다."));
  }

  @Test
  @DisplayName("사용자 HARD 삭제 실패 - 유예 기간 미경과")
  void hard_delete_user_NotYet() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    willThrow(new UserDeletedNotYetException())
        .given(userService)
        .deleteUserHard(eq(userId), eq(userId));

    // when
    var result = mockMvc.perform(delete("/api/users/{userId}/hard", userId)
        .header("Deokhugam-Request-User-ID", userId));

    // then
    result.andExpect(status().isBadRequest());
    result.andExpect(jsonPath("$.message").value("탈퇴 후 24시간이 지나야 영구 삭제가 가능합니다."));
  }

  @Test
  @DisplayName("사용자 HARD 삭제 실패 - 필수 헤더 누락")
  void hard_delete_user_MissingHeader() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    // when
    var result = mockMvc.perform(delete("/api/users/{userId}/hard", userId));

    // then
    result.andExpect(status().isUnauthorized());
    result.andExpect(jsonPath("$.message").value("인증에 실패하였습니다."));
  }

}
