package com.redhorse.deokhugam.domain.alarm.controller;

import com.redhorse.deokhugam.domain.alarm.service.AlarmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlarmController.class)
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
                        // 🚨 수정: .param() 대신 .header()를 사용합니다!
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
}