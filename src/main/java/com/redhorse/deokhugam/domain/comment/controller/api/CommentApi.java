package com.redhorse.deokhugam.domain.comment.controller.api;

import com.redhorse.deokhugam.domain.comment.dto.CommentCreateRequest;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import com.redhorse.deokhugam.domain.comment.dto.CommentPageRequest;
import com.redhorse.deokhugam.domain.comment.dto.CommentUpdateRequest;
import com.redhorse.deokhugam.domain.comment.dto.CursorPageResponseCommentDto;
import com.redhorse.deokhugam.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "댓글 관리", description = "댓글 관련 API")
public interface CommentApi {

  @Operation(summary = "댓글 등록", description = "새로운 댓글을 등록합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "댓글 등록 성공",
          content = @Content(schema = @Schema(implementation = CommentDto.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청 (입력값 검증 실패)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "리뷰 정보 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 내부 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  ResponseEntity<CommentDto> create(
      @Parameter(description = "댓글 정보", required = true,
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = CommentCreateRequest.class)))
      @Valid @RequestBody CommentCreateRequest commentCreateRequest
  );

  @Operation(summary = "댓글 수정", description = "본인이 작성한 댓글을 수정합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "댓글 수정 성공",
          content = @Content(schema = @Schema(implementation = CommentDto.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청 (입력값 검증 실패, 요청자 ID 누락)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "403", description = "댓글 수정 권한 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "댓글 정보 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 내부 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  ResponseEntity<CommentDto> update(
      @Parameter(description = "댓글 ID", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
      @PathVariable UUID commentId,
      @Parameter(description = "요청자 ID", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
      @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId,
      @Parameter(description = "댓글 수정 정보", required = true,
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = CommentUpdateRequest.class)))
      @Valid @RequestBody CommentUpdateRequest commentUpdateRequest
  );

  @Operation(summary = "댓글 상세 정보 조회", description = "특정 댓글의 상세 정보를 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "댓글 조회 성공",
          content = @Content(schema = @Schema(implementation = CommentDto.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "댓글 정보 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 내부 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  ResponseEntity<CommentDto> find(
      @Parameter(description = "댓글 ID", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
      @PathVariable UUID commentId
  );

  @Operation(summary = "댓글 논리 삭제", description = "본인이 작성한 댓글을 논리적으로 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "댓글 삭제 성공"
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청 (요청자 ID 누락)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "403", description = "댓글 삭제 권한 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "댓글 정보 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 내부 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  ResponseEntity<Void> softDelete(
      @Parameter(description = "댓글 ID", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
      @PathVariable UUID commentId,
      @Parameter(description = "요청자 ID", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
      @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId
  );

  @Operation(summary = "댓글 물리 삭제", description = "본인이 작성한 댓글을 물리적으로 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "댓글 삭제 성공"
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청 (요청자 ID 누락)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "403", description = "댓글 삭제 권한 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "댓글 정보 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 내부 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  ResponseEntity<Void> hardDelete(
      @Parameter(description = "댓글 ID", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
      @PathVariable UUID commentId,
      @Parameter(description = "요청자 ID", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
      @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId
  );

  @Operation(summary = "리뷰 댓글 목록 조회", description = "특정 리뷰에 달린 댓글 목록을 시간순으로 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "댓글 목록 조회 성공",
          content = @Content(schema = @Schema(implementation = CursorPageResponseCommentDto.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청 (정렬 방향 오류, 페이지네이션 파라미터 오류, 리뷰 ID 누락)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "리뷰 정보 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 내부 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  ResponseEntity<CursorPageResponseCommentDto> findAll(
      @Valid @ParameterObject CommentPageRequest commentPageRequest
  );
}
