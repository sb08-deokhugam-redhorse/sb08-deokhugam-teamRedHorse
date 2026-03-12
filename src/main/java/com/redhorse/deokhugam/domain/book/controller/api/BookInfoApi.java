package com.redhorse.deokhugam.domain.book.controller.api;

import com.redhorse.deokhugam.global.exception.ErrorResponse;
import com.redhorse.deokhugam.infra.naver.dto.NaverBookDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "도서 관리")
public interface BookInfoApi
{
    @Operation(
            summary = "ISBN으로 도서 정보 조회",
            description = "Naver API를 통해 ISBN으로 도서 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "도서 정보 조회 성공",
                    content = @Content(mediaType = "*/*", schema = @Schema(implementation = NaverBookDto.class))),
            @ApiResponse(
                    responseCode = "400", description = "잘못된 ISBN 형식",
                    content = @Content(mediaType = "*/*", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404", description = "도서 정보 없음",
                    content = @Content(mediaType = "*/*", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "500", description = "서버 내부 오류",
                    content = @Content(mediaType = "*/*", schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<NaverBookDto> getBookInfo(
            @Parameter(description = "ISBN 번호", example = "9788965402602", required = true) String isbn
    );

    @Operation(summary = "OCR 기반 ISBN 인식",
            description = "OCR을 통해 ISBN을 인식합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ISBN 인식 성공",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 이미지 형식 또는 OCR 인식 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<String> getIsbnFromOcr(
            @Parameter(description = "도서 이미지", required = true)
            @RequestParam("image") MultipartFile image
    );
}
