package com.redhorse.deokhugam.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

  private final MDCLoggingInterceptor mdcLoggingInterceptor;
  private final AuthenticationInterceptor authenticationInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // MDC 로그 인터셉터
    registry.addInterceptor(mdcLoggingInterceptor)
        .addPathPatterns("/**")
        .order(1);

    // 인증 헤더확인 인터셉터
    registry
        .addInterceptor(authenticationInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns(
            // 인증 제외 대상
            "/",
            "/index.html",
            "/api/users",
            "/api/users/login",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/assets/**",
            "/images/**",
            "/favicon.ico"
        )
        .order(2);

  }
}
