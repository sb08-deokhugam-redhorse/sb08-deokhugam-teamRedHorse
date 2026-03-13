package com.redhorse.deokhugam.domain.alarm.service;

import com.redhorse.deokhugam.domain.alarm.entity.Alarm;
import com.redhorse.deokhugam.domain.alarm.exception.AlarmNotFoundException;
import com.redhorse.deokhugam.domain.alarm.exception.NoAlarmException;
import com.redhorse.deokhugam.domain.alarm.repository.AlarmRepository;
import com.redhorse.deokhugam.domain.alarm.service.impl.AlarmServiceImpl;
import com.redhorse.deokhugam.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("알림 읽은 상태 테스트")
class AlarmServiceAlarmReadUnitTest {

    @InjectMocks
    private AlarmServiceImpl alarmService;

    @Mock
    private AlarmRepository alarmRepository;

    @Test
    @DisplayName("단일 알림 읽음 처리 성공 - 해당 알림의 update()가 호출된다")
    void checkAlarm_Success() {
        // given
        UUID testAlarmId = UUID.randomUUID();
        UUID testUserId = UUID.randomUUID();

        Alarm mockAlarm = mock(Alarm.class);
        User mockUser = mock(User.class);

        given(alarmRepository.findById(testAlarmId)).willReturn(Optional.of(mockAlarm));
        given(mockAlarm.getUser()).willReturn(mockUser);
        given(mockUser.getId()).willReturn(testUserId);

        // when
        alarmService.checkAlarm(testAlarmId, testUserId);

        // then
        verify(mockAlarm, times(1)).update();
    }

    @Test
    @DisplayName("단일 알림 읽음 처리 실패 - 알림이 존재하지 않으면 예외 발생")
    void checkAlarm_NotFound() {
        // given
        UUID testAlarmId = UUID.randomUUID();
        UUID testUserId = UUID.randomUUID();

        given(alarmRepository.findById(testAlarmId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> alarmService.checkAlarm(testAlarmId, testUserId))
                .isInstanceOf(AlarmNotFoundException.class)
                .hasMessage("알림을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("전체 알림 읽음 처리 성공 - 리스트 내 모든 알림의 update()가 호출된다")
    void checkAllAlarm_Success() {
        // given
        UUID testUserId = UUID.randomUUID();
        Alarm mockAlarm1 = mock(Alarm.class);
        Alarm mockAlarm2 = mock(Alarm.class);

        given(alarmRepository.findAllAlarmByUserId(testUserId)).willReturn(List.of(mockAlarm1, mockAlarm2));

        // when
        alarmService.checkAllAlarm(testUserId);

        // then
        verify(mockAlarm1, times(1)).update();
        verify(mockAlarm2, times(1)).update();
    }

    @Test
    @DisplayName("전체 알림 읽음 처리 실패 - 알림 리스트가 비어있으면 예외 발생")
    void checkAllAlarm_EmptyList() {
        // given
        UUID testUserId = UUID.randomUUID();

        // 텅 빈 리스트 반환
        given(alarmRepository.findAllAlarmByUserId(testUserId)).willReturn(Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> alarmService.checkAllAlarm(testUserId))
                .isInstanceOf(NoAlarmException.class)
                .hasMessage("알림을 찾을 수 없습니다.");
    }
}