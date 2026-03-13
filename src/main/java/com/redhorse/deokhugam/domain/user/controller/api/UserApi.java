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
  @Operation(summary = "로그인")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "로그인 성공",
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
                           "timestamp": "2026-03-12T07:36:57.584959900Z",
                           "code": "VALIDATION_FAILED",
                           "message": "입력값 검증에 실패했습니다.",
                           "details": {
                               "password": "비밀번호는 필수 입력값입니다."
                           },
                           "exceptionType": "MethodArgumentNotValidException",
                           "status": 400
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "아이디 또는 비밀번호가 일치하지 않음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                          "timestamp": "2026-03-12T07:36:15.108924600Z",
                          "code": "LOGIN_FAILED",
                          "message": "아이디 또는 비밀번호가 일치하지 않습니다.",
                          "details": {},
                          "exceptionType": "UserLoginFailedException",
                          "status": 401
                      }
                      """
              )
          )
      )
  })
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
  @Operation(summary = "사용자 조회[단건]")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "사용자 조회 성공",
          content = @Content(
              schema = @Schema(implementation = UserDto.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "사용자를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                           "timestamp": "2026-03-12T07:37:50.272678200Z",
                           "code": "USER_NOT_FOUND",
                           "message": "사용자를 찾을 수 없습니다.",
                           "details": {
                               "user": "a01c54ca-c392-4175-a5ed-b488bb2e8c48"
                           },
                           "exceptionType": "UserNotFoundException",
                           "status": 404
                       }
                      """
              )
          )
      )
  })
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
  @Operation(summary = "사용자 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "사용자 수정 성공",
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
                           "timestamp": "2026-03-12T07:40:14.084987200Z",
                           "code": "VALIDATION_FAILED",
                           "message": "입력값 검증에 실패했습니다.",
                           "details": {
                               "nickname": "닉네임은 필수 입력값입니다."
                           },
                           "exceptionType": "MethodArgumentNotValidException",
                           "status": 400
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "403",
          description = "수정 권한 없음 (자신만 수정 가능)",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                           "timestamp": "2026-03-12T07:45:36.419661900Z",
                           "code": "UNAUTHORIZED_USER",
                           "message": "해당 요청에 대한 권한이 없습니다.",
                           "details": {
                               "header": "접근 권한 거부",
                               "description": "본인의 데이터만 접근할 수 있습니다."
                           },
                           "exceptionType": "AuthenticationException",
                           "status": 403
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "사용자를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                           "timestamp": "2026-03-12T07:39:36.670533200Z",
                           "code": "USER_NOT_FOUND",
                           "message": "사용자를 찾을 수 없습니다.",
                           "details": {
                               "user": "eea8d487-3a29-419e-b2bd-6ca1016dd82e"
                           },
                           "exceptionType": "UserNotFoundException",
                           "status": 404
                      }
                      """
              )
          )
      )
  })
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
  @Operation(summary = "사용자 삭제[SOFT]")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204",
          description = "사용자 논리 삭제 성공"
      ),
      @ApiResponse(
          responseCode = "403",
          description = "삭제 권한 없음 (자신만 삭제 가능)",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                           "timestamp": "2026-03-12T07:45:36.419661900Z",
                           "code": "UNAUTHORIZED_USER",
                           "message": "해당 요청에 대한 권한이 없습니다.",
                           "details": {
                               "header": "접근 권한 거부",
                               "description": "본인의 데이터만 접근할 수 있습니다."
                           },
                           "exceptionType": "AuthenticationException",
                           "status": 403
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "사용자를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                           "timestamp": "2026-03-12T07:46:20.791058600Z",
                           "code": "USER_NOT_FOUND",
                           "message": "사용자를 찾을 수 없습니다.",
                           "details": {
                               "user": "eea8d487-3a29-419e-b2bd-6ca1016dd82d"
                           },
                           "exceptionType": "UserNotFoundException",
                           "status": 404
                      }
                      """
              )
          )
      )
  })
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
  @Operation(summary = "사용자 삭제[HARD]")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204",
          description = "사용자 영구 삭제 성공"
      ),
      @ApiResponse(
          responseCode = "400",
          description = "삭제 불가 (논리 삭제 미처리 또는 기간 미충족)",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = {
                  @ExampleObject(
                      name = "논리 삭제 미처리",
                      value = """
                          {
                               "timestamp": "2026-03-12T07:47:17.525238100Z",
                               "code": "USER_NOT_SOFT_DELETED",
                               "message": "탈퇴 처리되지 않은 사용자입니다.",
                               "details": {
                                   "description": "영구 삭제는 소프트 삭제된 계정에 대해서만 가능합니다. 먼저 회원 탈퇴를 진행해 주세요."
                               },
                               "exceptionType": "UserNotSoftDeletedException",
                               "status": 400
                          }
                          """
                  ),
                  @ExampleObject(
                      name = "기간 미충족",
                      value = """
                          {
                               "timestamp": "2026-03-12T07:47:45.881871Z",
                               "code": "HARD_DELETE_NOT_ALLOWED_YET",
                               "message": "탈퇴 후 24시간이 지나야 영구 삭제가 가능합니다.",
                               "details": {
                                   "description": "탈퇴 후 24시간이 지나지 않았습니다."
                               },
                               "exceptionType": "UserDeletedNotYetException",
                               "status": 400
                          }
                          """
                  )
              }
          )
      ),
      @ApiResponse(
          responseCode = "403",
          description = "삭제 권한 없음 (자신만 삭제 가능)",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                      {
                           "timestamp": "2026-03-12T07:45:36.419661900Z",
                           "code": "UNAUTHORIZED_USER",
                           "message": "해당 요청에 대한 권한이 없습니다.",
                           "details": {
                               "header": "접근 권한 거부",
                               "description": "본인의 데이터만 접근할 수 있습니다."
                           },
                           "exceptionType": "AuthenticationException",
                           "status": 403
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "사용자를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(
                  value = """
                     {
                           "timestamp": "2026-03-12T07:46:20.791058600Z",
                           "code": "USER_NOT_FOUND",
                           "message": "사용자를 찾을 수 없습니다.",
                           "details": {
                               "user": "eea8d487-3a29-419e-b2bd-6ca1016dd82d"
                           },
                           "exceptionType": "UserNotFoundException",
                           "status": 404
                     }
                      """
              )
          )
      )
  })
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
