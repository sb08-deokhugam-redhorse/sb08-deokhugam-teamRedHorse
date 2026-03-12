package com.redhorse.deokhugam.domain.user.controller.api;

import com.redhorse.deokhugam.domain.user.dto.request.UserLoginRequest;
import com.redhorse.deokhugam.domain.user.dto.request.UserRegisterRequest;
import com.redhorse.deokhugam.domain.user.dto.request.UserUpdateRequest;
import com.redhorse.deokhugam.domain.user.dto.response.UserDto;
import com.redhorse.deokhugam.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


@Tag(name = "사용자 관리", description = "사용자 관련 API")
public interface UserApi {

  /**
   * =====================================
   * 사용자 생성
   * ======================================
   */
  @Operation(summary = "사용자 등록")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201",
          description = "사용자가 성공적으로 생성됨",
          content = @Content(
              schema = @Schema(implementation = UserDto.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "입력값 검증 실패",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "timestamp": "2026-03-12T06:50:21.502630800Z",
                        "status": 400,
                        "code": "VALIDATION_FAILED",
                        "message": "입력값 검증에 실패했습니다.",
                        "details": {
                          "email": "이메일 형식이 올바르지 않습니다."
                        },
                        "exceptionType": "MethodArgumentNotValidException"
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "409",
          description = "이미 등록된 이메일입니다",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                        "timestamp": "2026-03-12T06:46:30.598088Z",
                        "status": 409,
                        "code": "USER_DUPLICATE",
                        "message": "이미 존재하는 사용자입니다.",
                        "details": {
                          "email": "se***@gmail.com"
                        },
                        "exceptionType": "UserDuplicateException"
                      }
                      """
              )
          )
      )
  })
  ResponseEntity<UserDto> createUser(
      @Parameter(
          description = "사용자 생성 정보",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = UserRegisterRequest.class)
          )
      )
      UserRegisterRequest request
  );

  /**
   * =====================================
   * 로그인
   * ======================================
   */
  ResponseEntity<UserDto> login(
      @Parameter(
          description = "로그인 정보",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = UserLoginRequest.class)
          )
      )
      UserLoginRequest request
  );

  /**
   * =====================================
   * 사용자 조회[단건]
   * ======================================
   */
  ResponseEntity<UserDto> getUser(
      @Parameter(
          description = "사용자 ID",
          schema = @Schema(
              type = "string",
              format = "uuid",
              example = "550e8400-e29b-41d4-a716-446655440000"
          )
      )
      UUID userId
  );


  /**
   * =====================================
   * 사용자 수정
   * ======================================
   */
  ResponseEntity<UserDto> updateUser(
      @Parameter(
          description = "사용자 ID",
          schema = @Schema(
              type = "string",
              format = "uuid",
              example = "550e8400-e29b-41d4-a716-446655440000"
          )
      )
      UUID userId,
      @Parameter(
          description = "요청 사용자 ID (헤더)",
          schema = @Schema(
              type = "string",
              format = "uuid",
              example = "550e8400-e29b-41d4-a716-446655440000"
          )
      )
      UUID requestUserId,
      @Parameter(
          description = "사용자 수정 정보",
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = UserUpdateRequest.class)
          )
      )
      UserUpdateRequest request
  );

  /**
   * =====================================
   * 사용자 삭제[SOFT]
   * ======================================
   */
  ResponseEntity deleteUserSoft(
      @Parameter(
          description = "요청 사용자 ID (헤더)",
          schema = @Schema(
              type = "string",
              format = "uuid",
              example = "550e8400-e29b-41d4-a716-446655440000"
          )
      )
      UUID requestUserId,
      @Parameter(
          description = "사용자 ID",
          schema = @Schema(
              type = "string",
              format = "uuid",
              example = "550e8400-e29b-41d4-a716-446655440000"
          )
      )
      UUID userId
  );

  /**
   * =====================================
   * 사용자 삭제[HARD]
   * ======================================
   */
  ResponseEntity deleteUserHard(
      @Parameter(
          description = "요청 사용자 ID (헤더)",
          schema = @Schema(
              type = "string",
              format = "uuid",
              example = "550e8400-e29b-41d4-a716-446655440000"
          )
      )
      UUID requestUserId,
      @Parameter(
          description = "사용자 ID",
          schema = @Schema(
              type = "string",
              format = "uuid",
              example = "550e8400-e29b-41d4-a716-446655440000"
          )
      )
      UUID userId
  );
}
