package com.redhorse.deokhugam.domain.user.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.redhorse.deokhugam.domain.user.dto.request.UserRegisterRequest;
import com.redhorse.deokhugam.domain.user.dto.response.UserDto;
import com.redhorse.deokhugam.domain.user.entity.User;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("UserMapper 단위 테스트")
class UserMapperTest {

  // MapStruct 구현체를 직접 가져옴 (Spring 없이 테스트 가능)
  private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

  @Test
  @DisplayName("UserRegisterRequest -> User 엔티티 변환 테스트")
  void toEntity() {
    // given
    UserRegisterRequest request = new UserRegisterRequest(
        "seongjo.park@gmail.com",
        "박성조",
        "password123!"
    );

    // when
    User user = userMapper.toEntity(request);

    // then
    assertThat(user).isNotNull();
    assertThat(user.getEmail()).isEqualTo(request.email());
    assertThat(user.getNickname()).isEqualTo(request.nickname());
    assertThat(user.getPassword()).isEqualTo(request.password());
    assertThat(user.isDeleted()).isFalse();
  }

  @Test
  @DisplayName("User 엔티티 -> UserDto 변환 테스트")
  void toUserDto() {
    // given
    User user = new User(
        "seongjo.park@gmail.com",
        "박성조",
        "password123!"
    );
    UUID userId = UUID.randomUUID();
    ReflectionTestUtils.setField(user, "id", userId);
    ReflectionTestUtils.setField(user, "createdAt", Instant.now());

    // when
    UserDto dto = userMapper.toUserDto(user);

    // then
    assertThat(dto).isNotNull();
    assertThat(dto.id()).isEqualTo(userId);
    assertThat(dto.email()).isEqualTo(user.getEmail());
    assertThat(dto.nickname()).isEqualTo(user.getNickname());
    assertThat(dto.createdAt()).isEqualTo(user.getCreatedAt());
  }
}
