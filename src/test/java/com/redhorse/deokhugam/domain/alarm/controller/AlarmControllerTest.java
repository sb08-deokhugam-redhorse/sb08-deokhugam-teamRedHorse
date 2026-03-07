package com.redhorse.deokhugam.domain.alarm.controller;

import com.redhorse.deokhugam.domain.alarm.dto.CursorPageResponseNotificationDto;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationListRequest;
import com.redhorse.deokhugam.domain.alarm.service.AlarmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlarmController.class)
@ActiveProfiles("test")
class AlarmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AlarmService alarmService;

    @Test
    @DisplayName("단건 알림 읽음 처리 API 성공 - 200 OK 반환")
    void updateAlarmToRead_Single_Success() throws Exception {
        // given
        String testAlarmId = UUID.randomUUID().toString();
        String testUserId = UUID.randomUUID().toString();

        doNothing().when(alarmService).checkAlarm(any(UUID.class), any(UUID.class));

        // when & then
        mockMvc.perform(patch("/api/notifications/{notificationId}", testAlarmId)
                        .header("Deokhugam-Request-Id", testUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").exists());

        verify(alarmService).checkAlarm(any(UUID.class), any(UUID.class));
    }

    @Test
    @DisplayName("전체 알림 읽음 처리 API 성공 - 200 OK 반환")
    void updateAlarmToRead_All_Success() throws Exception {
        // given
        String testUserId = UUID.randomUUID().toString();

        doNothing().when(alarmService).checkAllAlarm(any(UUID.class));

        // when & then
        mockMvc.perform(patch("/api/notifications/read-all")
                        .header("Deokhugam-Request-Id", testUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(alarmService).checkAllAlarm(any(UUID.class));
    }

    @Test
    @DisplayName("단건 알림 읽음 처리 실패 - 필수 헤더 누락 시 500 Server Error 반환")
    void updateAlarmToRead_Single_MissingHeader() throws Exception {
        // given
        String testAlarmId = UUID.randomUUID().toString();

        // when & then
        mockMvc.perform(patch("/api/notifications/{notificationId}", testAlarmId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("알림 목록 조회 테스트 - 서비스 호출 및 응답 반환 검증")
    void getAlarmList_Success() {
        // given
        UUID userId = UUID.fromString("10000000-0000-0000-0000-000000000005");
        NotificationListRequest request = new NotificationListRequest(
                userId, "DESC", null, null, 20
        );

        CursorPageResponseNotificationDto expectedResponse = new CursorPageResponseNotificationDto(
                List.of(),
                null,
                null,
                0,
                0L,
                false
        );

        when(alarmService.getAlarmList(request)).thenReturn(expectedResponse);

        // when
        AlarmController alarmController = null;
        ResponseEntity<CursorPageResponseNotificationDto> result = alarmController.getAlarmList(request);

        // then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResponse, result.getBody());
        verify(alarmService, times(1)).getAlarmList(request);
    }
}