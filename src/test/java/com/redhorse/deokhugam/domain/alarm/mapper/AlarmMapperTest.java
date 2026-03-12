package com.redhorse.deokhugam.domain.alarm.mapper;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationDto;
import com.redhorse.deokhugam.domain.alarm.entity.Alarm;
import com.redhorse.deokhugam.domain.dashboard.dto.poweruser.PowerUserDto;
import com.redhorse.deokhugam.domain.dashboard.entity.PowerUser;
import com.redhorse.deokhugam.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("알림 매퍼 테스트")
class AlarmMapperTest {
    private final AlarmMapper alarmMapper = new AlarmMapperImpl();

    @Test
    @DisplayName("Alarm -> NotificationDto 변환 성공 및 읽지 않음(false) 상태 확인")
    void alarmToNotificationDto_NotConfirmed() {
        // given
        UUID userId = UUID.randomUUID();
        Instant now = Instant.now();

        User mockUser = mock(User.class);
        given(mockUser.getId()).willReturn(userId);

        Alarm mockAlarm = mock(Alarm.class);
        given(mockAlarm.getUser()).willReturn(mockUser);
        // 생성일과 수정일이 같으면 읽지 않은 상태(false)여야 함
        given(mockAlarm.getCreatedAt()).willReturn(now);
        given(mockAlarm.getUpdatedAt()).willReturn(now);

        // when
        NotificationDto result = alarmMapper.alarmToNotificationDto(mockAlarm);

        // then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.confirmed()).isFalse(); // isConfirmed 로직 검증
    }

    @Test
    @DisplayName("Alarm -> NotificationDto 변환 성공 및 읽음(true) 상태 확인")
    void alarmToNotificationDto_Confirmed() {
        // given
        UUID userId = UUID.randomUUID();
        Instant createdAt = Instant.now();
        Instant updatedAt = createdAt.plusSeconds(3600);

        User mockUser = mock(User.class);
        given(mockUser.getId()).willReturn(userId);

        Alarm mockAlarm = mock(Alarm.class);
        given(mockAlarm.getUser()).willReturn(mockUser);
        // 생성일과 수정일이 다르면 읽은 상태(true)여야 함
        given(mockAlarm.getCreatedAt()).willReturn(createdAt);
        given(mockAlarm.getUpdatedAt()).willReturn(updatedAt);

        // when
        NotificationDto result = alarmMapper.alarmToNotificationDto(mockAlarm);

        // then
        assertThat(result).isNotNull();
        assertThat(result.confirmed()).isTrue(); // isConfirmed 로직 검증
    }

    @Test
    @DisplayName("isConfirmed 로직 검증 - 시간이 null인 경우 false 반환")
    void alarmToNotificationDto_TimeIsNull_ThenFalse() {
        // given
        Alarm mockAlarm = mock(Alarm.class);
        given(mockAlarm.getUser()).willReturn(mock(User.class));
        // 둘 중 하나라도 null이면 false 반환
        given(mockAlarm.getCreatedAt()).willReturn(null);

        // when
        NotificationDto result = alarmMapper.alarmToNotificationDto(mockAlarm);

        // then
        assertThat(result.confirmed()).isFalse();
    }

    @Test
    @DisplayName("PowerUser -> PowerUserDto 변환 성공")
    void toPowerUserDto_Success() {
        // given
        UUID userId = UUID.randomUUID();
        String nickname = "파워유저닉네임";
        Long rank = 1L;

        User mockUser = mock(User.class);
        given(mockUser.getId()).willReturn(userId);
        given(mockUser.getNickname()).willReturn(nickname);

        PowerUser mockPowerUser = mock(PowerUser.class);
        given(mockPowerUser.getUser()).willReturn(mockUser);
        given(mockPowerUser.getRanking()).willReturn(rank);

        // when
        PowerUserDto result = alarmMapper.toPowerUserDto(mockPowerUser);

        // then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.nickname()).isEqualTo(nickname);
        assertThat(result.rank()).isEqualTo(rank);
    }
}
