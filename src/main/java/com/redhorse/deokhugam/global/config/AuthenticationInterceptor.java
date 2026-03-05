package com.redhorse.deokhugam.global.config;

import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import com.redhorse.deokhugam.global.exception.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

  private final UserRepository userRepository;

  @Override
  public boolean preHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler
  ) throws Exception {
    String userIdHeader = request.getHeader("Deokhugam-Request-User-ID");

    // 헤더 누락 확인
    if (userIdHeader == null || userIdHeader.isEmpty()) {
      throw new AuthenticationException();
    }

    UUID userId;

    try {
      // String → UUID 변환할 때
      // String이 UUID 형식이 아니면 IllegalArgumentException 에러나기때문에 try문 사용
      userId = UUID.fromString(userIdHeader);
    } catch (IllegalArgumentException e) {
      throw new AuthenticationException();
    }

    // 실제 유저가 존재하는지 확인
    userRepository.findById(userId)
        .orElseThrow(AuthenticationException::new);

    return true;
  }
}
