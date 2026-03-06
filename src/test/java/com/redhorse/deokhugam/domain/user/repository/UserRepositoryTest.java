package com.redhorse.deokhugam.domain.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.global.config.JpaConfig;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaConfig.class)
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

  @Test
  @DisplayName("이메일로 유저 조회")
  void findByEmail() {
    // given
    String email = "seongjo.park@gmail.com";

    User user = new User(
        email,
        "박성조",
        "Thisistest123***"
    );
    userRepository.save(user);

    // when
    var foundUser = userRepository.findByEmail(email);
    var notFoundUser = userRepository.findByEmail("notfound@gmail.com");

    // then
    assertThat(foundUser).isPresent();
    assertThat(
        foundUser.get().getEmail()
    ).isEqualTo(email);
    assertThat(notFoundUser).isEmpty();
  }

  @Test
  @DisplayName("ID로 사용자 조회")
  void findById() {
    // given
    User user = new User(
        "seongjo.park@gmail.com",
        "박성조",
        "Thisistest123***"
    );
    User findUser = userRepository.save(user);

    // when
    var foundUser = userRepository.findById(findUser.getId());
    var notFoundUser = userRepository.findById(UUID.randomUUID());

    // then
    assertThat(foundUser).isPresent();
    assertThat(
        foundUser.get().getEmail()
    ).isEqualTo(user.getEmail());
    assertThat(notFoundUser).isEmpty();
  }
}
