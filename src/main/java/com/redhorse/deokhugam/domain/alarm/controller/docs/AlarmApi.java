package com.redhorse.deokhugam.domain.alarm.controller.docs;

import com.redhorse.deokhugam.domain.alarm.dto.CursorPageResponseNotificationDto;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationDto;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationListRequest;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationUpdateRequest;
import com.redhorse.deokhugam.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "알림 관리", description = "알림 관련 API")
public interface AlarmApi {
    @Operation(
            summary = "알림 읽음 상태 업데이트",
            description = "특정 알림의 ID를 받아 읽음 상태로 변경합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "알림 상태 업데이트 성공",
                            content = @Content(schema = @Schema(implementation = NotificationDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 검증 실패, 요청자 ID 누락)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "알림 수정 권한 없음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "알림 정보 없음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            }
    )
    @RequestBody(
            required = true,
            content = @Content(schema = @Schema(implementation = NotificationUpdateRequest.class))
    )
    ResponseEntity<NotificationUpdateRequest> updateAlarmToRead(
            @Parameter(description = "알림 UUID", required = true) UUID notificationId,
            @Parameter(description = "사용자 ID (Header)", in = ParameterIn.HEADER, required = true) UUID deokhugamRequestUserID
    );

    @Operation(
            summary = "모든 알림 읽음 처리",
            description = "사용자의 모든 알림을 읽음 상태로 처리합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "알림 읽음 처리 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 (사용자 ID 누락)"),
                    @ApiResponse(responseCode = "403", description = "알림 수정 권한 없음"),
                    @ApiResponse(responseCode = "404", description = "알림 정보 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
            }
    )
    void updateAlarmAllToRead(
            @Parameter(description = "사용자 ID (Header)", in = ParameterIn.HEADER, required = true) UUID deokhugamRequestUserID
    );

    @Operation(
            summary = "알림 목록 조회",
            description = "사용자의 알림 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "알림 목록 조회 성공",
                            content = @Content(schema = @Schema(implementation = CursorPageResponseNotificationDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 (정렬 방향 오류, 페이지네이션 파라미터 오류, 사용자 ID 누락)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "사용자 정보 없음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            }
    )
    ResponseEntity<CursorPageResponseNotificationDto> getAlarmList(
            @Parameter(description = "페이징 및 필터 조건", required = true)
            @Valid NotificationListRequest request
    );
}
