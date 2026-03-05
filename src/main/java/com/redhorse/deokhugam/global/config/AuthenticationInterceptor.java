package com.redhorse.deokhugam.global.config;

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

    try {
      // UUID 패턴인지 확인
      // String → UUID 변환할 때, String이 UUID 형식이 아니면 IllegalArgumentException 에러 발생하기 때문에 try문 사용
      UUID.fromString(userIdHeader);
    } catch (IllegalArgumentException e) {
      throw new AuthenticationException();
    }

    return true;
  }
}
