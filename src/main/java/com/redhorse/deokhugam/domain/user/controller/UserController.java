package com.redhorse.deokhugam.domain.user.controller;

import com.redhorse.deokhugam.domain.user.dto.request.UserLoginRequest;
import com.redhorse.deokhugam.domain.user.dto.request.UserRegisterRequest;
import com.redhorse.deokhugam.domain.user.dto.request.UserUpdateRequest;
import com.redhorse.deokhugam.domain.user.dto.response.UserDto;
import com.redhorse.deokhugam.domain.user.service.UserService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/users")
@Slf4j
public class UserController {

  private final UserService userService;

  @PostMapping()
  public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserRegisterRequest request) {
    log.info("[User-Controller] 요청 시작: content = {}", request);

    UserDto userDto = userService.createUser(request);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(userDto);
  }

  @PostMapping("/login")
  public ResponseEntity<UserDto> login(@Valid @RequestBody UserLoginRequest request) {
    log.info("[User-Controller] 요청 시작: content = {}", request);

    UserDto userDto = userService.login(request);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userDto);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserDto> getUser(@PathVariable UUID userId) {
    log.info("[User-Controller] 요청 시작: content = {}", userId);

    UserDto userDto = userService.getUser(userId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userDto);
  }

  @PatchMapping("/{userId}")
  public ResponseEntity<UserDto> updateUser(
      @PathVariable UUID userId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId,
      @Valid @RequestBody UserUpdateRequest request
  ) {
    log.info("[User-Controller] 요청 시작: content = userId: {}, request = {}", userId, request);

    UserDto userDto = userService.updateUser(userId, requestUserId ,request);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userDto);
  }
}
