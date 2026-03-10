package com.redhorse.deokhugam.domain.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhorse.deokhugam.domain.book.dto.request.BookCreateRequest;
import com.redhorse.deokhugam.domain.book.dto.request.BookUpdateRequest;
import com.redhorse.deokhugam.domain.book.dto.response.BookDto;
import com.redhorse.deokhugam.domain.book.dto.response.CursorPageResponseBookDto;
import com.redhorse.deokhugam.domain.book.exception.BookNotFoundException;
import com.redhorse.deokhugam.domain.book.exception.IsbnDuplicateException;
import com.redhorse.deokhugam.domain.book.service.BookService;
import com.redhorse.deokhugam.global.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(GlobalExceptionHandler.class)
@WebMvcTest(BookController.class)
@DisplayName("BookController Slice Test")
class BookControllerTest
{
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private BookService bookService;

    private BookCreateRequest bookCreateRequest;
    private BookUpdateRequest bookUpdateRequest;
    private BookDto bookDto;
    private BookDto updatedBookDto;
    private UUID bookId;

    @BeforeEach
    void setUp() {
        bookCreateRequest = new BookCreateRequest(
                "자바 프로그래밍",
                "김자바",
                "자바 소개",
                "출판사A",
                LocalDate.of(2024, 1, 1),
                "9788965745464"
        );

        bookUpdateRequest = new BookUpdateRequest(
                "수정된 자바 프로그래밍",
                "수정된 김자바",
                "수정된 소개",
                "수정된 출판사",
                LocalDate.of(2025, 1, 1)
        );

        bookDto = new BookDto(
                UUID.randomUUID(), "자바 프로그래밍", "김자바", "자바 소개", "출판사A",
                LocalDate.of(2024, 1, 1), "9788965745464", null, 0, 0.0,
                Instant.now(), Instant.now()
        );

        updatedBookDto = new BookDto(
                UUID.randomUUID(), "수정된 자바 프로그래밍", "수정된 김자바", "수정된 소개", "수정된 출판사",
                LocalDate.of(2025, 1, 1), "9788965745464", null, 0, 0.0,
                Instant.now(), Instant.now()
        );

        bookId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("도서 등록 POST /api/books")
    class CreateBook {
        @Test
        @DisplayName("성공 - 유효한 요청이면 201 Created를 반환한다.")
        void success_withValidRequest_returns201() throws Exception {
            given(bookService.create(any(BookCreateRequest.class), any())).willReturn(bookDto);

            MockMultipartFile bookData = new MockMultipartFile(
                    "bookData", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(bookCreateRequest)
            );

            mockMvc.perform(multipart("/api/books")
                            .file(bookData)
                            .header("Deokhugam-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title").value("자바 프로그래밍"))
                    .andExpect(jsonPath("$.author").value("김자바"));
        }

        @Test
        @DisplayName("성공 - ISBN 없이 요청하면 201 Created를 반환한다.")
        void success_withoutIsbn_returns201() throws Exception {
            given(bookService.create(any(BookCreateRequest.class), any())).willReturn(bookDto);

            BookCreateRequest requestWithoutIsbn = new BookCreateRequest(
                    "자바 프로그래밍", "김자바", "자바 소개", "출판사A",
                    LocalDate.of(2024, 1, 1), null
            );

            MockMultipartFile bookData = new MockMultipartFile(
                    "bookData", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(requestWithoutIsbn)
            );

            mockMvc.perform(multipart("/api/books")
                            .file(bookData)
                            .header("Deokhugam-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("실패 - 제목이 없으면 400 Bad Request를 반환한다.")
        void fail_withBlankTitle_returns400() throws Exception {
            BookCreateRequest invalidRequest = new BookCreateRequest(
                    "", "김자바", "자바 소개", "출판사A",
                    LocalDate.of(2024, 1, 1), "9788965745464"
            );

            MockMultipartFile bookData = new MockMultipartFile(
                    "bookData", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(invalidRequest)
            );

            mockMvc.perform(multipart("/api/books")
                            .file(bookData)
                            .header("Deokhugam-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("실패 - ISBN 형식이 올바르지 않으면 400 Bad Request를 반환한다.")
        void fail_withInvalidIsbn_returns400() throws Exception {
            BookCreateRequest invalidRequest = new BookCreateRequest(
                    "자바 프로그래밍", "김자바", "자바 소개", "출판사A",
                    LocalDate.of(2024, 1, 1), "12345"
            );

            MockMultipartFile bookData = new MockMultipartFile(
                    "bookData", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(invalidRequest)
            );

            mockMvc.perform(multipart("/api/books")
                            .file(bookData)
                            .header("Deokhugam-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("실패 - ISBN이 중복되면 409 Conflict를 반환한다.")
        void fail_withDuplicateIsbn_returns409() throws Exception {
            given(bookService.create(any(BookCreateRequest.class), any()))
                    .willThrow(new IsbnDuplicateException("9788965745464"));

            MockMultipartFile bookData = new MockMultipartFile(
                    "bookData", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(bookCreateRequest)
            );

            mockMvc.perform(multipart("/api/books")
                            .file(bookData)
                            .header("Deokhugam-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET /api/books/{bookId}")
    class GetBookById {
        @Test
        @DisplayName("성공 - 유효한 bookId로 조회하면 200 OK를 반환한다")
        void success_withValidBookId_returns200() throws Exception {
            // given
            given(bookService.findById(bookId)).willReturn(bookDto);

            // when & then
            mockMvc.perform(get("/api/books/{bookId}", bookId)
                            .header("Deokhugam-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("자바 프로그래밍"))
                    .andExpect(jsonPath("$.author").value("김자바"));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 bookId면 404 Not Found를 반환한다")
        void fail_withNonExistentBookId_returns404() throws Exception {
            // given
            given(bookService.findById(bookId)).willThrow(new BookNotFoundException(bookId));

            // when & then
            mockMvc.perform(get("/api/books/{bookId}", bookId)
                            .header("Deokhugam-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/books")
    class GetAllBooks {
        @Test
        @DisplayName("성공 - 파라미터 없이 조회하면 200 OK를 반환한다")
        void success_withNoParams_returns200() throws Exception {
            // given
            CursorPageResponseBookDto response = new CursorPageResponseBookDto(
                    List.of(bookDto), null, null, 1, 1L, false
            );
            given(bookService.getBooks(null, "title", "DESC", null, null, 50)).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/books")
                            .header("Deokhugam-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].title").value("자바 프로그래밍"))
                    .andExpect(jsonPath("$.hasNext").value(false))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        @DisplayName("성공 - 키워드로 검색하면 200 OK를 반환한다")
        void success_withKeyword_returns200() throws Exception {
            // given
            CursorPageResponseBookDto response = new CursorPageResponseBookDto(
                    List.of(bookDto), null, null, 1, 1L, false
            );
            given(bookService.getBooks("자바", "title", "DESC", null, null, 50)).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/books")
                            .param("keyword", "자바")
                            .header("Deokhugam-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].title").value("자바 프로그래밍"));
        }

        @Test
        @DisplayName("성공 - 결과가 없으면 빈 목록을 반환한다")
        void success_withNoResult_returnsEmptyList() throws Exception {
            // given
            CursorPageResponseBookDto response = new CursorPageResponseBookDto(
                    List.of(), null, null, 0, 0L, false
            );
            given(bookService.getBooks("없는책", "title", "DESC", null, null, 50)).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/books")
                            .param("keyword", "없는책")
                            .header("Deokhugam-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));
        }
    }

    @Nested
    @DisplayName("도서 수정 PATCH /api/books/{bookId}")
    class PatchBook {
        @Test
        @DisplayName("성공 - 유효한 요청이면 200 OK를 반환한다")
        void success_withValidRequest_returns200() throws Exception {
            given(bookService.update(any(UUID.class), any(BookUpdateRequest.class), any())).willReturn(updatedBookDto);

            MockMultipartFile bookData = new MockMultipartFile(
                    "bookData", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(bookUpdateRequest)
            );

            mockMvc.perform(multipart(HttpMethod.PATCH, "/api/books/{bookId}", bookId)
                            .file(bookData)
                            .header("Deokhugam-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("수정된 자바 프로그래밍"));
        }

        @Test
        @DisplayName("실패 - 제목이 없으면 400 Bad Request를 반환한다")
        void fail_withBlankTitle_returns400() throws Exception {
            BookUpdateRequest invalidRequest = new BookUpdateRequest(
                    "", "수정된 김자바", "수정된 소개", "수정된 출판사",
                    LocalDate.of(2025, 1, 1)
            );

            MockMultipartFile bookData = new MockMultipartFile(
                    "bookData", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(invalidRequest)
            );

            mockMvc.perform(multipart(HttpMethod.PATCH, "/api/books/{bookId}", bookId)
                            .file(bookData)
                            .header("Deokhugam-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 도서면 404 Not Found를 반환한다")
        void fail_withNonExistentBook_returns404() throws Exception {
            given(bookService.update(any(UUID.class), any(BookUpdateRequest.class), any()))
                    .willThrow(new BookNotFoundException(bookId));

            MockMultipartFile bookData = new MockMultipartFile(
                    "bookData", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(bookUpdateRequest)
            );

            mockMvc.perform(multipart(HttpMethod.PATCH, "/api/books/{bookId}", bookId)
                            .file(bookData)
                            .header("Deokhugam-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("도서 삭제(논리) DELETE /api/books/{bookId}")
    class SoftDeleteBook {
        @Test
        @DisplayName("성공 - 도서를 논리 삭제하면 204 No Content를 반환한다")
        void success_softDelete_returns204() throws Exception {
            // given
            willDoNothing().given(bookService).softDelete(bookId);

            // when & then
            mockMvc.perform(delete("/api/books/{bookId}", bookId)
                            .header("Deokhugam-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 도서면 404 Not Found를 반환한다")
        void fail_withNonExistentBook_returns404() throws Exception {
            // given
            willThrow(new BookNotFoundException(bookId)).given(bookService).softDelete(bookId);

            // when & then
            mockMvc.perform(delete("/api/books/{bookId}", bookId)
                            .header("Deokhugam-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("도서 삭제(물리) DELETE /api/books/{bookId}/hard")
    class HardDeleteBook {
        @Test
        @DisplayName("성공 - 도서를 물리 삭제하면 204 No Content를 반환한다")
        void success_hardDelete_returns204() throws Exception {
            // given
            willDoNothing().given(bookService).hardDelete(bookId);

            // when & then
            mockMvc.perform(delete("/api/books/{bookId}/hard", bookId)
                            .header("Deokhugam-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 도서면 404 Not Found를 반환한다")
        void fail_withNonExistentBook_returns404() throws Exception {
            // given
            willThrow(new BookNotFoundException(bookId)).given(bookService).hardDelete(bookId);

            // when & then
            mockMvc.perform(delete("/api/books/{bookId}/hard", bookId)
                            .header("Deokhugam-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isNotFound());
        }
    }
}