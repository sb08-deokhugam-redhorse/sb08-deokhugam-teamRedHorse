package com.redhorse.deokhugam.domain.book.controller;

import com.redhorse.deokhugam.global.exception.GlobalExceptionHandler;
import com.redhorse.deokhugam.infra.naver.NaverBookProvider;
import com.redhorse.deokhugam.infra.naver.dto.NaverBookDto;
import com.redhorse.deokhugam.infra.naver.exception.NaverBookNotFoundException;
import com.redhorse.deokhugam.infra.ocr.OcrProvider;
import com.redhorse.deokhugam.infra.ocr.exception.IsbnNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(GlobalExceptionHandler.class)
@WebMvcTest(BookInfoController.class)
@DisplayName("BookInfoController Slice Test")
public class BookInfoControllerTest
{
    @Autowired private MockMvc mockMvc;

    @MockitoBean private NaverBookProvider naverBookProvider;
    @MockitoBean private OcrProvider ocrProvider;

    private NaverBookDto naverBookDto;

    @BeforeEach
    void setUp() {
        naverBookDto = new NaverBookDto(
                "자바 프로그래밍",
                "김자바",
                "출판사",
                "소개",
                LocalDate.of(2024, 1, 1),
                "9788912345678",
                null
        );
    }

    @Nested
    @DisplayName("GET /api/books/info")
    class GetBookInfo {
        @Test
        @DisplayName("성공 - 유효한 ISBN으로 도서 정보를 조회하면 200 OK를 반환한다")
        void success_withValidIsbn_returns200() throws Exception {
            // given
            given(naverBookProvider.getBookInfoByIsbn("9788912345678")).willReturn(naverBookDto);

            // when & then
            mockMvc.perform(get("/api/books/info")
                            .param("isbn", "9788912345678")
                            .header("Deokhugam-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("자바 프로그래밍"))
                    .andExpect(jsonPath("$.author").value("김자바"))
                    .andExpect(jsonPath("$.isbn").value("9788912345678"))
                    .andExpect(jsonPath("$.publishedDate").value("2024-01-01"));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ISBN이면 404 Not Found를 반환한다")
        void fail_withNotFoundIsbn_returns404() throws Exception {
            // given
            given(naverBookProvider.getBookInfoByIsbn("9780000000000"))
                    .willThrow(new NaverBookNotFoundException("9780000000000"));

            // when & then
            mockMvc.perform(get("/api/books/info")
                            .param("isbn", "9780000000000")
                            .header("Deokhugam-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/books/isbn/ocr")
    class GetIsbnFromOcr {
        @Test
        @DisplayName("성공 - 유효한 이미지로 ISBN을 추출하면 200 OK를 반환한다")
        void success_withValidImage_returns200() throws Exception {
            // given
            MockMultipartFile image = new MockMultipartFile(
                    "image", "book.jpg", MediaType.IMAGE_JPEG_VALUE, "image-content".getBytes()
            );
            given(ocrProvider.extractIsbn(any())).willReturn(List.of("9788912345678"));

            // when & then
            mockMvc.perform(multipart("/api/books/isbn/ocr").file(image)
                            .header("Deokhugam-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0]").value("9788912345678"));
        }

        @Test
        @DisplayName("실패 - ISBN을 추출할 수 없으면 400 Bad Request를 반환한다")
        void fail_withUnreadableImage_returns400() throws Exception {
            // given
            MockMultipartFile image = new MockMultipartFile(
                    "image", "blur.jpg", MediaType.IMAGE_JPEG_VALUE, "blur-content".getBytes()
            );
            given(ocrProvider.extractIsbn(any()))
                    .willThrow(new IsbnNotFoundException());

            // when & then
            mockMvc.perform(multipart("/api/books/isbn/ocr").file(image)
                            .header("Deokhugam-Request-User-ID", UUID.randomUUID().toString()))
                    .andExpect(status().isBadRequest());
        }
    }
}
