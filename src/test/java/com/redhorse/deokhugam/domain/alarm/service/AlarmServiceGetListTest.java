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
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("알림 목록 조회 테스트")
class AlarmServiceGetListTest {

    @Mock
    private AlarmRepository alarmRepository;

    @Mock
    private AlarmMapper alarmMapper;

    @InjectMocks
    private AlarmServiceImpl alarmService;

    @Test
    @DisplayName("알림 목록 조회 테스트 - 다음 페이지가 있는 경우 커서가 올바르게 생성되는지 확인")
    void getAlarmList_WithNextPage() {
        // given
        UUID userId = UUID.randomUUID();
        int limit = 2;
        NotificationListRequest request = new NotificationListRequest(userId, "DESC", null, null, limit);

        Alarm alarm1 = mock(Alarm.class);
        Alarm alarm2 = mock(Alarm.class);
        Alarm alarm3 = mock(Alarm.class);

        UUID lastId = UUID.randomUUID();
        Instant lastCreatedAt = Instant.now().minusSeconds(100);

        given(alarm2.getId()).willReturn(lastId);
        given(alarm2.getCreatedAt()).willReturn(lastCreatedAt);

        List<Alarm> alarmList = List.of(alarm1, alarm2, alarm3);
        Slice<Alarm> alarmSlice = new SliceImpl<>(alarmList, PageRequest.of(0, limit + 1), true);

        given(alarmRepository.getAllAlarmsDesc(any(), any())).willReturn(alarmSlice);
        given(alarmRepository.countAlarmsByUserId(userId)).willReturn(10L);

        given(alarmMapper.alarmToNotificationDto(any())).willReturn(mock(NotificationDto.class));

        // when
        CursorPageResponseNotificationDto result = alarmService.getAlarmList(request);

        // then
        assertAll(
                () -> assertEquals(limit, result.contents().size(), "결과 리스트는 limit 크기만큼 잘려야 함"),
                () -> assertTrue(result.hasNext(), "데이터가 limit보다 많으므로 hasNext는 true여야 함"),
                () -> assertEquals(lastId.toString(), result.nextCursor(), "limit 번째 데이터의 ID가 커서가 되어야 함"),
                () -> assertEquals(lastCreatedAt, result.nextAfter(), "limit 번째 데이터의 시간이 nextAfter가 되어야 함"),
                () -> assertEquals(10L, result.totalElements())
        );
    }

    @Test
    @DisplayName("알림 목록 조회 테스트 - 데이터가 없는 경우")
    void getAlarmList_Empty() {
        // given
        UUID userId = UUID.randomUUID();
        NotificationListRequest request = new NotificationListRequest(userId, "DESC", null, null, 20);

        Slice<Alarm> emptySlice = new SliceImpl<>(List.of(), PageRequest.of(0, 20), false);
        given(alarmRepository.getAllAlarmsDesc(any(), any())).willReturn(emptySlice);
        given(alarmRepository.countAlarmsByUserId(userId)).willReturn(0L);

        // when
        CursorPageResponseNotificationDto result = alarmService.getAlarmList(request);

        // then
        assertAll(
                () -> assertTrue(result.contents().isEmpty()),
                () -> assertNull(result.nextCursor()),
                () -> assertNull(result.nextAfter()),
                () -> assertFalse(result.hasNext()),
                () -> assertEquals(0, result.size())
        );
    }
}