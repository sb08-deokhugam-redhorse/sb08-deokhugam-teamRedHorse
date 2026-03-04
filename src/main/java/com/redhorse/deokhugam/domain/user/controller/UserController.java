package com.redhorse.deokhugam.domain.user.controller;

import com.redhorse.deokhugam.domain.user.dto.request.UserLoginRequest;
import com.redhorse.deokhugam.domain.user.dto.request.UserRegisterRequest;
import com.redhorse.deokhugam.domain.user.dto.response.UserDto;
import com.redhorse.deokhugam.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    UserDto userDto = userService.createUser(request);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(userDto);
  }

  @PostMapping("/login")
  public ResponseEntity<UserDto> login(@RequestBody UserLoginRequest request) {

    UserDto userDto = userService.login(request);

    return  ResponseEntity
        .status(HttpStatus.OK)
        .body(userDto);
  }
}
