package com.redhorse.deokhugam.domain.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.redhorse.deokhugam.config.TestJpaConfig;
import com.redhorse.deokhugam.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(TestJpaConfig.class)
@DisplayName("UserRepository 슬라이스 테스트")
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("이메일로 유저 존재 여부 확인")
  void existsUserByEmail() {
    // given
    String email = "seongjo.park@gmail.com";
    User user = new User(
        email,
        "박성조",
        "Thisistest123***"
    );
    userRepository.save(user);

    // when
    boolean exists = userRepository.existsUserByEmail(email);
    boolean notExists = userRepository.existsUserByEmail("seongjo.park222@gmail.com");

    // then
    assertThat(exists).isTrue();
    assertThat(notExists).isFalse();
  }

  @Test
  @DisplayName("유저 저장 및 조회")
  void saveAndFindUser() {
    // given
    User user = new User(
        "seongjo.park@gmail.com",
        "박성조",
        "Thisistest123***"
    );

    // when
    User savedUser = userRepository.save(user);

    // then
    assertThat(savedUser.getId()).isNotNull();
    assertThat(savedUser.getEmail()).isEqualTo("seongjo.park@gmail.com");
    assertThat(savedUser.getCreatedAt()).isNotNull();
  }
}
