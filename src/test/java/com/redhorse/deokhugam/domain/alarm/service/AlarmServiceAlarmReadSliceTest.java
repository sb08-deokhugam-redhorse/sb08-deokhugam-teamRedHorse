package com.redhorse.deokhugam.domain.alarm.service;

import com.redhorse.deokhugam.domain.alarm.entity.Alarm;
import com.redhorse.deokhugam.domain.alarm.exception.AlarmNotFoundException;
import com.redhorse.deokhugam.domain.alarm.mapper.AlarmMapper;
import com.redhorse.deokhugam.domain.alarm.repository.AlarmRepository;
import com.redhorse.deokhugam.domain.alarm.service.impl.AlarmServiceImpl;
import com.redhorse.deokhugam.domain.comment.controller.CommentController;
import com.redhorse.deokhugam.domain.review.repository.ReviewRepository;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@WebMvcTest(AlarmServiceImpl.class)
class AlarmServiceAlarmReadSliceTest {

    @Autowired
    private AlarmServiceImpl alarmService;

    @MockBean
    private AlarmRepository alarmRepository;
    @MockBean
    private ReviewRepository reviewRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AlarmMapper alarmMapper;

    @Test
    @DisplayName("스프링 빈 환경에서 checkAllAlarm 호출 시 정상적으로 전체 업데이트가 수행된다")
    void checkAllAlarm_InSpringContext() {
        // given
        UUID testUserId = UUID.randomUUID();
        Alarm mockAlarm = mock(Alarm.class);
        given(mockAlarm.getMessage()).willReturn("스프링 테스트 알림");

        given(alarmRepository.findAllAlarmByUserId(testUserId)).willReturn(List.of(mockAlarm));

        // when
        alarmService.checkAllAlarm(testUserId);

        // then
        verify(mockAlarm).update();
    }

    @Test
    @DisplayName("스프링 빈 환경에서 알림 단건 조회 실패 시 예외가 발생한다")
    void checkAlarm_NotFound_InSpringContext() {
        // given
        UUID testAlarmId = UUID.randomUUID();
        UUID testUserId = UUID.randomUUID();

        given(alarmRepository.findById(testAlarmId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> alarmService.checkAlarm(testAlarmId, testUserId))
                .isInstanceOf(AlarmNotFoundException.class)
                .hasMessage("알림을 찾을 수 없습니다.");
    }
}