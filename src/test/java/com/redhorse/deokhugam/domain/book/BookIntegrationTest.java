package com.redhorse.deokhugam.domain.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhorse.deokhugam.domain.book.dto.request.BookCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@DisplayName("BookController Integration Test")
class BookIntegrationTest
{
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private BookCreateRequest bookCreateRequest;
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
    }

    @Nested
    @DisplayName("도서 등록 POST /api/books")
    class CreateBook {
        @Test
        @DisplayName("성공 - 유효한 요청이면 201 Created와 도서 정보를 반환한다")
        void success_withValidRequest_returns201() throws Exception {
            MockMultipartFile bookData = new MockMultipartFile(
                    "bookData", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(bookCreateRequest)
            );

            mockMvc.perform(multipart("/api/books")
                            .file(bookData))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title").value("자바 프로그래밍"))
                    .andExpect(jsonPath("$.author").value("김자바"))
                    .andExpect(jsonPath("$.isbn").value("9788965745464"));
        }

        @Test
        @DisplayName("성공 - 썸네일 이미지와 함께 요청하면 201 Created를 반환한다")
        void success_withThumbnailImage_returns201() throws Exception {
            MockMultipartFile bookData = new MockMultipartFile(
                    "bookData", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(bookCreateRequest)
            );
            MockMultipartFile thumbnailImage = new MockMultipartFile(
                    "thumbnailImage", "thumbnail.jpg", MediaType.IMAGE_JPEG_VALUE,
                    "image".getBytes()
            );

            mockMvc.perform(multipart("/api/books")
                            .file(bookData)
                            .file(thumbnailImage))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title").value("자바 프로그래밍"));
        }

        @Test
        @DisplayName("성공 - ISBN 없이 요청하면 201 Created를 반환한다")
        void success_withoutIsbn_returns201() throws Exception {
            BookCreateRequest requestWithoutIsbn = new BookCreateRequest(
                    "자바 프로그래밍", "김자바", "자바 소개", "출판사A",
                    LocalDate.of(2024, 1, 1), null
            );

            MockMultipartFile bookData = new MockMultipartFile(
                    "bookData", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(requestWithoutIsbn)
            );

            mockMvc.perform(multipart("/api/books")
                            .file(bookData))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.isbn").doesNotExist());
        }

        @Test
        @DisplayName("실패 - 제목이 없으면 400 Bad Request를 반환한다")
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
                            .file(bookData))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("실패 - ISBN 형식이 올바르지 않으면 400 Bad Request를 반환한다")
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
                            .file(bookData))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("실패 - ISBN이 중복되면 409 Conflict를 반환한다")
        void fail_withDuplicateIsbn_returns409() throws Exception {
            MockMultipartFile bookData = new MockMultipartFile(
                    "bookData", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(bookCreateRequest)
            );

            mockMvc.perform(multipart("/api/books")
                    .file(bookData))
                    .andExpect(status().isCreated());

            MockMultipartFile bookData2 = new MockMultipartFile(
                    "bookData", "", MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(bookCreateRequest)
            );

            mockMvc.perform(multipart("/api/books")
                            .file(bookData2))
                    .andExpect(status().isConflict());
        }
    }
}