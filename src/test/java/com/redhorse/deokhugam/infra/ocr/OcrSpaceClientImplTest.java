package com.redhorse.deokhugam.infra.ocr;

import com.redhorse.deokhugam.infra.ocr.exception.ImageSizeExceededException;
import com.redhorse.deokhugam.infra.ocr.exception.OcrProcessingException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OcrSpaceClientImpl Unit Test")
class OcrSpaceClientImplTest
{
    private MockWebServer mockWebServer;
    private OcrSpaceClientImpl ocrSpaceClientImpl;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        RestClient restClient = RestClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        ocrSpaceClientImpl = new OcrSpaceClientImpl(restClient, "test-api-key", mockWebServer.url("/").toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Nested
    @DisplayName("텍스트 추출")
    class ExtractText {
        @Test
        @DisplayName("성공 - 유효한 이미지로 텍스트를 추출하면 parsedText를 반환한다")
        void success_withValidImage_returnsParsedText() {
            // given
            String responseBody = """
                    {
                        "ParsedResults": [{"ParsedText": "9791234567890", "FileParseExitCode": 1}],
                        "OCRExitCode": 1,
                        "IsErroredOnProcessing": false
                    }
                    """;
            mockWebServer.enqueue(new MockResponse()
                    .setBody(responseBody)
                    .addHeader("Content-Type", "application/json")
                    .setResponseCode(200));

            MockMultipartFile image = new MockMultipartFile(
                    "image", "book.jpg", MediaType.IMAGE_JPEG_VALUE, "image-content".getBytes()
            );

            // when
            String result = ocrSpaceClientImpl.extractText(image);

            // then
            assertThat(result).isEqualTo("9791234567890");
        }

        @Test
        @DisplayName("실패 - OCR 처리 중 에러가 발생하면 OcrProcessingException을 던진다")
        void fail_withOcrError_throwsOcrProcessingException() {
            // given
            String responseBody = """
                    {
                        "ParsedResults": [],
                        "OCRExitCode": 99,
                        "IsErroredOnProcessing": true
                    }
                    """;
            mockWebServer.enqueue(new MockResponse()
                    .setBody(responseBody)
                    .addHeader("Content-Type", "application/json")
                    .setResponseCode(200));

            MockMultipartFile image = new MockMultipartFile(
                    "image", "book.jpg", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes()
            );

            // when & then
            assertThatThrownBy(() -> ocrSpaceClientImpl.extractText(image))
                    .isInstanceOf(OcrProcessingException.class);
        }

        @Test
        @DisplayName("실패 - 파일 크기가 1MB를 초과하면 ImageSizeExceededException을 던진다")
        void fail_withOversizedImage_throwsImageSizeExceededException() {
            // given
            MockMultipartFile image = mock(MockMultipartFile.class);
            when(image.getSize()).thenReturn(1024 * 1024 + 1L);

            // when & then
            assertThatThrownBy(() -> ocrSpaceClientImpl.extractText(image))
                    .isInstanceOf(ImageSizeExceededException.class);
        }

        @Test
        @DisplayName("실패 - 서버 오류가 발생하면 OcrProcessingException을 던진다")
        void fail_withServerError_throwsOcrProcessingException() {
            // given
            mockWebServer.enqueue(new MockResponse().setResponseCode(500));

            MockMultipartFile image = new MockMultipartFile(
                    "image", "book.jpg", MediaType.IMAGE_JPEG_VALUE, "hello".getBytes()
            );

            // when & then
            assertThatThrownBy(() -> ocrSpaceClientImpl.extractText(image))
                    .isInstanceOf(OcrProcessingException.class);
        }
    }
}