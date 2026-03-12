package com.redhorse.deokhugam.domain.dashboard.controller.docs;

import com.redhorse.deokhugam.domain.dashboard.dto.request.DashboardRequest;
import com.redhorse.deokhugam.domain.dashboard.dto.response.CursorPageResponsePopularBookDto;
import com.redhorse.deokhugam.domain.dashboard.dto.response.CursorPageResponsePopularReviewkDto;
import com.redhorse.deokhugam.domain.dashboard.dto.response.CursorPageResponsePowerUserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

@Tag(name = "대시보드 관리", description = "대시보드 관련 API")
public interface DashboardApi {
    @Operation(
            summary = "인기 리뷰 목록 조회",
            description = "기간별 인기 리뷰 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "인기 리뷰 목록 조회 성공",
                            content = @Content(schema = @Schema(implementation = CursorPageResponsePopularReviewkDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 (랭킹 기간 오류, 정렬 방향 오류 등)",
                            content = @Content(schema = @Schema(implementation = CursorPageResponsePopularReviewkDto.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = CursorPageResponsePopularReviewkDto.class))),
            }
    )
    ResponseEntity<CursorPageResponsePopularReviewkDto> getPopularReviews(
            @Parameter(description = "대시보드 요청 DTO", required = true)
            @Valid DashboardRequest request
    );

    @Operation(
            summary = "파워 유저 목록 조회",
            description = "기간별 파워 유저 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "파워 유저 목록 조회 성공",
                            content = @Content(schema = @Schema(implementation = CursorPageResponsePowerUserDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 (랭킹 기간 오류, 정렬 방향 오류 등)",
                            content = @Content(schema = @Schema(implementation = CursorPageResponsePowerUserDto.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = CursorPageResponsePowerUserDto.class))),
            }
    )
    ResponseEntity<CursorPageResponsePowerUserDto>  getPowerUsers(
            @Parameter(description = "대시보드 요청 DTO)",required = true)
            @Valid DashboardRequest request
    );

    @Operation(
            summary = "인기 도서 목록 조회",
            description = "기간별 인기 도서 목록을 조회합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "인기 도서 목록 조회 성공",
                            content = @Content(schema = @Schema(implementation = CursorPageResponsePopularBookDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 (랭킹 기간 오류, 정렬 방향 오류 등)",
                            content = @Content(schema = @Schema(implementation = CursorPageResponsePopularBookDto.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = CursorPageResponsePopularBookDto.class))),
            }
    )
    ResponseEntity<CursorPageResponsePopularBookDto> getPopularBooks(
            @Parameter(description = "대시보드 요청 DTO", required = true)
            @Valid DashboardRequest request
    );
}
