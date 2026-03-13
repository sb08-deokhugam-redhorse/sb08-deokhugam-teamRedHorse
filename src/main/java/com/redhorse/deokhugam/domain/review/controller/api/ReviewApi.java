package com.redhorse.deokhugam.domain.review.controller.api;

import com.redhorse.deokhugam.domain.review.dto.CursorPageResponseReviewDto;
import com.redhorse.deokhugam.domain.review.dto.ReviewCreateRequest;
import com.redhorse.deokhugam.domain.review.dto.ReviewDto;
import com.redhorse.deokhugam.domain.review.dto.ReviewLikeDto;
import com.redhorse.deokhugam.domain.review.dto.ReviewSearchRequest;
import com.redhorse.deokhugam.domain.review.dto.ReviewUpdateRequest;
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

@Tag(name = "리뷰 관리", description = "리뷰 관련 API")
public interface ReviewApi {

  @Operation(summary = "리뷰 등록")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "리뷰 등록 성공",
          content = @Content(schema = @Schema(implementation = ReviewDto.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "도서, 유저 정보 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "409", description = "이미 작성된 리뷰 존재",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 내부 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  }
  )
  ResponseEntity<ReviewDto> create(
      @Parameter(description = "리뷰 생성 정보", required = true,
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ReviewCreateRequest.class)
          )
      )
      @RequestBody @Valid ReviewCreateRequest request
  );

  @Operation(summary = "리뷰 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "리뷰 수정 성공",
          content = @Content(schema = @Schema(implementation = ReviewDto.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "403", description = "리뷰 수정 권한 없음",
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
  }
  )
  ResponseEntity<ReviewDto> update(
      @Parameter(
          description = "리뷰 ID", required = true,
          example = "123e4567-e89b-12d3-a456-426614174000"
      )
      @PathVariable UUID reviewId,
      @Parameter(
          description = "요청자 ID", required = true,
          example = "123e4567-e89b-12d3-a456-426614174000"
      )
      @RequestHeader("Deokhugam-Request-User-ID") UUID userId,
      @Parameter(description = "리뷰 수정 정보", required = true,
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ReviewUpdateRequest.class)
          )
      )
      @RequestBody @Valid ReviewUpdateRequest request
  );

  @Operation(summary = "리뷰 논리 삭제")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "리뷰 논리 삭제 성공"
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "403", description = "리뷰 삭제 권한 없음",
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
  }
  )
  ResponseEntity<Void> delete(
      @Parameter(
          description = "리뷰 ID", required = true,
          example = "123e4567-e89b-12d3-a456-426614174000"
      )
      @PathVariable UUID reviewId,
      @Parameter(
          description = "요청자 ID", required = true,
          example = "123e4567-e89b-12d3-a456-426614174000"
      )
      @RequestHeader("Deokhugam-Request-User-ID") UUID userId
  );

  @Operation(summary = "리뷰 물리 삭제")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "리뷰 물리 삭제 성공"
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "403", description = "리뷰 삭제 권한 없음",
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
  }
  )
  ResponseEntity<Void> hardDelete(
      @Parameter(
          description = "리뷰 ID", required = true,
          example = "123e4567-e89b-12d3-a456-426614174000"
      )
      @PathVariable UUID reviewId,
      @Parameter(
          description = "요청자 ID", required = true,
          example = "123e4567-e89b-12d3-a456-426614174000"
      )
      @RequestHeader("Deokhugam-Request-User-ID") UUID userId
  );

  @Operation(summary = "리뷰 좋아요")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "리뷰 좋아요 성공",
          content = @Content(schema = @Schema(implementation = ReviewLikeDto.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청",
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
  }
  )
  ResponseEntity<ReviewLikeDto> like(
      @Parameter(
          description = "리뷰 ID", required = true,
          example = "123e4567-e89b-12d3-a456-426614174000"
      )
      @PathVariable UUID reviewId,
      @Parameter(
          description = "요청자 ID", required = true,
          example = "123e4567-e89b-12d3-a456-426614174000"
      )
      @RequestHeader("Deokhugam-Request-User-ID") UUID userId
  );

  @Operation(summary = "리뷰 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "리뷰 목록 조회 성공",
          content = @Content(schema = @Schema(implementation = CursorPageResponseReviewDto.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "사용자 정보 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 내부 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  }
  )
  ResponseEntity<CursorPageResponseReviewDto> findAll(
      @ParameterObject ReviewSearchRequest request,
      @Parameter(
          description = "요청자 ID", required = true,
          example = "123e4567-e89b-12d3-a456-426614174000"
      )
      @RequestHeader("Deokhugam-Request-User-ID") UUID userId
  );

  @Operation(summary = "리뷰 상세 정보 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "리뷰 상세 정보 조회 성공",
          content = @Content(schema = @Schema(implementation = ReviewDto.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청",
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
  }
  )
  ResponseEntity<ReviewDto> findById(
      @Parameter(
          description = "리뷰 ID", required = true,
          example = "123e4567-e89b-12d3-a456-426614174000"
      )
      @PathVariable UUID reviewId,
      @Parameter(
          description = "요청자 ID", required = true,
          example = "123e4567-e89b-12d3-a456-426614174000"
      )
      @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId
  );


}
