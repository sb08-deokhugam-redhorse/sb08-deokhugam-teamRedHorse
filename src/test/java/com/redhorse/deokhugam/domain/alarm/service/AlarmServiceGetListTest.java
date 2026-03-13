package com.redhorse.deokhugam.domain.alarm.service;

import com.redhorse.deokhugam.domain.alarm.dto.CursorPageResponseNotificationDto;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationDto;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationListRequest;
import com.redhorse.deokhugam.domain.alarm.entity.Alarm;
import com.redhorse.deokhugam.domain.alarm.mapper.AlarmMapper;
import com.redhorse.deokhugam.domain.alarm.repository.AlarmRepository;
import com.redhorse.deokhugam.domain.alarm.service.impl.AlarmServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("알림 조회 테스트")
class AlarmServiceGetListTest {

    @InjectMocks
    private AlarmServiceImpl alarmService;

    @Mock
    private AlarmRepository alarmRepository;

    @Mock
    private AlarmMapper alarmMapper;

    @Test
    @DisplayName("알림 목록 조회 테스트 - 데이터가 많은 경우 (hasNext = true)")
    void getAlarmList_HasNext() {
        // given
        UUID userId = UUID.randomUUID();
        int limit = 10;
        NotificationListRequest request = new NotificationListRequest(userId, "DESC", null, null, limit);

        List<Alarm> alarms = new ArrayList<>();
        UUID lastId = UUID.randomUUID();
        Instant lastCreatedAt = Instant.now();

        // limit + 1 개의 데이터를 만들어서 hasNext가 true가 되도록 유도
        for (int i = 0; i < limit + 1; i++) {
            Alarm alarm = mock(Alarm.class);
            if (i == limit - 1) { // 정확히 limit 번째 데이터(인덱스 limit-1)의 정보 저장 (다음 커서용)
                given(alarm.getId()).willReturn(lastId);
                given(alarm.getCreatedAt()).willReturn(lastCreatedAt);
            }
            alarms.add(alarm);
        }

        Slice<Alarm> slice = new SliceImpl<>(alarms, PageRequest.of(0, limit + 1), true);

        // 변경점: getAllAlarmsDesc가 아닌 getAllAlarms 호출
        given(alarmRepository.getAllAlarms(eq(request), any(Pageable.class))).willReturn(slice);
        given(alarmRepository.countAlarmsByUserId(userId)).willReturn(20L);
        given(alarmMapper.alarmToNotificationDto(any(Alarm.class))).willReturn(mock(NotificationDto.class));

        // when
        CursorPageResponseNotificationDto result = alarmService.getAlarmList(request);

        // then
        assertAll(
                () -> assertEquals(limit, result.content().size(), "결과 리스트는 limit 크기만큼 잘려야 함"),
                () -> assertTrue(result.hasNext(), "데이터가 limit보다 많으므로 hasNext는 true여야 함"),
                () -> assertEquals(lastId.toString(), result.nextCursor(), "limit 번째 데이터의 ID가 커서가 되어야 함"),
                () -> assertEquals(lastCreatedAt, result.nextAfter(), "limit 번째 데이터의 시간이 nextAfter가 되어야 함"),
                () -> assertEquals(20L, result.totalElements())
        );
    }

    @Test
    @DisplayName("알림 목록 조회 테스트 - 데이터가 없는 경우")
    void getAlarmList_Empty() {
        // given
        UUID userId = UUID.randomUUID();
        NotificationListRequest request = new NotificationListRequest(userId, "DESC", null, null, 20);

        Slice<Alarm> emptySlice = new SliceImpl<>(List.of(), PageRequest.of(0, 20), false);

        // 변경점: getAllAlarmsDesc가 아닌 getAllAlarms 호출
        given(alarmRepository.getAllAlarms(eq(request), any(Pageable.class))).willReturn(emptySlice);
        given(alarmRepository.countAlarmsByUserId(userId)).willReturn(0L);

        // when
        CursorPageResponseNotificationDto result = alarmService.getAlarmList(request);

        // then
        assertAll(
                () -> assertTrue(result.content().isEmpty()),
                () -> assertNull(result.nextCursor()),
                () -> assertNull(result.nextAfter()),
                () -> assertFalse(result.hasNext()),
                () -> assertEquals(0L, result.totalElements())
        );
    }
}