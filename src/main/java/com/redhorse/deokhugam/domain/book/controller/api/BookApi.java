package com.redhorse.deokhugam.domain.book.controller.api;

import com.redhorse.deokhugam.domain.book.dto.request.BookCreateRequest;
import com.redhorse.deokhugam.domain.book.dto.request.BookUpdateRequest;
import com.redhorse.deokhugam.domain.book.dto.response.BookDto;
import com.redhorse.deokhugam.domain.book.dto.response.CursorPageResponseBookDto;
import com.redhorse.deokhugam.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hibernate.query.SortDirection;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.UUID;

@Tag(name = "도서 관리", description = "도서 관련 API")
public interface BookApi
{
    @Operation(summary = "도서 등록", description = "새로운 도서를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "도서 등록 성공",
                    content = @Content(schema = @Schema(implementation = BookDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 검증 실패, ISBN 형식 오류 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "중복된 ISBN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<BookDto> createBook(
            @Parameter(description = "도서 정보", required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BookCreateRequest.class))) BookCreateRequest bookCreateRequest,
            @Parameter(description = "도서 썸네일 이미지", required = false,
                    content = @Content(schema = @Schema(type = "string", format = "binary"))) MultipartFile thumbnailImage
    );

    @Operation(summary = "도서 정보 수정", description = "도서 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "도서 정보 수정 성공",
                    content = @Content(schema = @Schema(implementation = BookDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 검증 실패, ISBN 형식 오류 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "도서 정보 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "ISBN 중복",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<BookDto> updateBook(
            @Parameter(description = "도서 ID", example = "123e4567-e89b-12d3-a456-426614174000", required = true) UUID bookID,
            @Parameter(description = "수정할 도서 정보", required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BookUpdateRequest.class))) BookUpdateRequest bookUpdateRequest,
            @Parameter(description = "수정할 도서 썸네일 이미지", required = false,
                    content = @Content(schema = @Schema(type = "string", format = "binary"))) MultipartFile thumbnailImage
    );

    @Operation(summary = "도서 목록 조회", description = "검색 조건에 맞는 도서 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "도서 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = CursorPageResponseBookDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (정렬 기준 오류, 페이지네이션 파라미터 오류 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<CursorPageResponseBookDto> getAllBooks(
            @Parameter(description = "검색어 (제목 | 저자 | ISBN)",example = "자바") String keyword,
            @Parameter(description = "정렬 기준(title | publishedDate | rating | reviewCount)", schema = @Schema(defaultValue = "title", example = "title")) String orderBy,
            @Parameter(description = "정렬 방향", schema = @Schema(implementation = SortDirection.class, defaultValue = "DESC", example = "DESC")) String direction,
            @Parameter(description = "커서 페이지네이션 커서") String cursor,
            @Parameter(description = "보조 커서 (created At)") Instant after,
            @Parameter(description = "페이지 크기") int limit
    );

    @Operation(summary = "도서 정보 상세 조회", description = "도서의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "도서 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = BookDto.class))),
            @ApiResponse(responseCode = "404", description = "도서 정보 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<BookDto> getBookById(@Parameter(description = "도서 ID", example = "123e4567-e89b-12d3-a456-426614174000", required = true) UUID bookId);

    @Operation(summary = "도서 논리 삭제", description = "도서를 논리적으로 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "도서 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "도서 정보 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류"),
    })
    ResponseEntity<Void> softDeleteBook(@Parameter(description = "도서 ID", example = "123e4567-e89b-12d3-a456-426614174000", required = true) UUID bookId);

    @Operation(summary = "도서 물리 삭제", description = "도서를 물리적으로 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "도서 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "도서 정보 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류"),
    })
    ResponseEntity<Void> hardDeleteBook(@Parameter(description = "도서 ID", example = "123e4567-e89b-12d3-a456-426614174000", required = true) UUID bookId);
}
