package com.redhorse.deokhugam.domain.alarm.controller.docs;

import com.redhorse.deokhugam.domain.alarm.dto.CursorPageResponseNotificationDto;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationListRequest;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface AlarmApi {
    @Operation(
            summary = "단일 알림 읽음 처리",
            description = "특정 알림의 ID를 받아 읽음 상태로 변경합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "알림 상태 업데이트 성공",
                            content = @Content(schema = @Schema(implementation = NotificationUpdateRequest.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 검증 실패, 요청자 ID 누락)"),
                    @ApiResponse(responseCode = "403", description = "알림 수정 권한 없음"),
                    @ApiResponse(responseCode = "404", description = "알림 정보 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
            }
    )
    ResponseEntity<NotificationUpdateRequest> updateAlarmToRead(
            @Parameter(description = "알림 UUID", required = true) UUID notificationId,
            @Parameter(description = "사용자 ID (Header)", in = ParameterIn.HEADER, required = true) UUID deokhugamRequestUserID
    );

    @Operation(
            summary = "모든 알림 읽음 처리",
            description = "현재 사용자의 모든 미확인 알림을 읽음 처리합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "전체 읽음 처리 완료")
            }
    )
    void updateAlarmAllToRead(
            @Parameter(description = "사용자 ID (Header)", in = ParameterIn.HEADER, required = true) UUID deokhugamRequestUserID
    );

    @Operation(
            summary = "알림 목록 조회 (커서 기반 페이징)",
            description = "사용자의 알림 목록을 최신순으로 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = CursorPageResponseNotificationDto.class)))
            }
    )
    ResponseEntity<CursorPageResponseNotificationDto> getAlarmList(
            @Parameter(description = "페이징 및 필터 조건", required = true)
            @Valid NotificationListRequest request
    );
}
