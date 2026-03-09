package com.redhorse.deokhugam.domain.alarm.controller;

import com.redhorse.deokhugam.domain.alarm.dto.CursorPageResponseNotificationDto;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationListRequest;
import com.redhorse.deokhugam.domain.alarm.service.AlarmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlarmController.class)
@ActiveProfiles("test")
class AlarmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
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
    void getAlarmList_Success() throws Exception {
        // 1. Given: 테스트 데이터 및 Mock 서비스 동작 정의
        UUID userId = UUID.fromString("10000000-0000-0000-0000-000000000005");

        CursorPageResponseNotificationDto expectedResponse = new CursorPageResponseNotificationDto(
                List.of(), null, null, 0, 0L, false
        );

        // AlarmService가 어떤 NotificationListRequest를 받더라도 준비된 응답을 반환하도록 설정
        when(alarmService.getAlarmList(any(NotificationListRequest.class))).thenReturn(expectedResponse);

        // 2. When & Then: MockMvc를 통해 컨트롤러 엔드포인트 호출 및 검증
        mockMvc.perform(get("/api/notifications")
                        .param("userId", userId.toString())
                        .param("direction", "DESC")
                        .param("limit", "20") // "size"에서 DTO 필드명인 "limit"으로 수정됨
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 200 OK 응답 확인
                .andExpect(jsonPath("$.hasNext").value(false)) // 응답 바디의 특정 필드 값 검증
                .andExpect(jsonPath("$.size").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));

        // 서비스 메서드가 실제로 1번 호출되었는지 검증
        verify(alarmService, times(1)).getAlarmList(any(NotificationListRequest.class));
    }
}