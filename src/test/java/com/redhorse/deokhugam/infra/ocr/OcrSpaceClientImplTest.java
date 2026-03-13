package com.redhorse.deokhugam.infra.ocr;

import com.redhorse.deokhugam.infra.ocr.dto.OcrSpaceResponse;
import com.redhorse.deokhugam.infra.ocr.exception.ImageSizeExceededException;
import com.redhorse.deokhugam.infra.ocr.exception.OcrProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OcrSpaceClientImpl Unit Test")
class OcrSpaceClientImplTest
{
    @Mock private RestClient.Builder restClientBuilder;
    @Mock private RestClient restClient;
    @Mock private RestClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock private RestClient.RequestBodySpec requestBodySpec;
    @Mock private RestClient.ResponseSpec responseSpec;

    private OcrSpaceClientImpl ocrSpaceClientImpl;

    @BeforeEach
    void setUp() {
        given(restClientBuilder.requestFactory(any())).willReturn(restClientBuilder);
        given(restClientBuilder.build()).willReturn(restClient);

        ocrSpaceClientImpl = new OcrSpaceClientImpl(
                restClientBuilder,
                "ocr-api-key",
                "https://test-ocr.com",
                3000,
                5000
        );

        ReflectionTestUtils.setField(ocrSpaceClientImpl, "ocrApiKey", "test-api-key");
        ReflectionTestUtils.setField(ocrSpaceClientImpl, "ocrUrl", "https://api.ocr.space/parse/image");
    }

    @Nested
    @DisplayName("텍스트 추출")
    class ExtractText {
        @Test
        @DisplayName("성공 - 유효한 이미지에서 텍스트를 추출한다.")
        void success_extractText_returnsText() {
            // given
            MultipartFile image = mock(MultipartFile.class);

            given(image.getSize()).willReturn(1024L);
            given(image.getResource()).willReturn(mock(org.springframework.core.io.Resource.class));
            given(image.getOriginalFilename()).willReturn("image.jpg");

            OcrSpaceResponse response = new OcrSpaceResponse(
                    List.of(new OcrSpaceResponse.ParseResult("9791234567890", 1)),
                    1,
                    false
            );

            given(restClient.post()).willReturn(requestBodyUriSpec);
            given(requestBodyUriSpec.uri(anyString())).willReturn(requestBodySpec);
            given(requestBodySpec.contentType(any())).willReturn(requestBodySpec);
            given(requestBodySpec.accept(any())).willReturn(requestBodySpec);
            given(requestBodySpec.header(anyString(), anyString())).willReturn(requestBodySpec);
            given(requestBodySpec.body(any(Object.class))).willReturn(requestBodySpec);
            given(requestBodySpec.retrieve()).willReturn(responseSpec);
            given(responseSpec.body(OcrSpaceResponse.class)).willReturn(response);

            // when
            String result = ocrSpaceClientImpl.extractText(image);

            // then
            assertThat(result).isEqualTo("9791234567890");
        }

        @Test
        @DisplayName("실패 - OCR 처리 중 에러가 발생하면 OcrProcessingException을 던진다")
        void fail_withOcrError_throwsOcrProcessingException() {
            // given
            MultipartFile image = mock(MultipartFile.class);
            given(image.getSize()).willReturn(1024L);
            given(image.getResource()).willReturn(mock(org.springframework.core.io.Resource.class));
            given(image.getOriginalFilename()).willReturn("test.jpg");

            OcrSpaceResponse response = new OcrSpaceResponse(
                    null,
                    99,
                    true // isErroredOnProcessing = true
            );

            given(restClient.post()).willReturn(requestBodyUriSpec);
            given(requestBodyUriSpec.uri(anyString())).willReturn(requestBodySpec);
            given(requestBodySpec.contentType(any())).willReturn(requestBodySpec);
            given(requestBodySpec.accept(any())).willReturn(requestBodySpec);
            given(requestBodySpec.header(anyString(), anyString())).willReturn(requestBodySpec);
            given(requestBodySpec.body(any())).willReturn(requestBodySpec);
            given(requestBodySpec.retrieve()).willReturn(responseSpec);
            given(responseSpec.body(OcrSpaceResponse.class)).willReturn(response);

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
            MultipartFile image = mock(MultipartFile.class);
            given(image.getSize()).willReturn(1024L);
            given(image.getResource()).willReturn(mock(org.springframework.core.io.Resource.class));
            given(image.getOriginalFilename()).willReturn("test.jpg");

            given(restClient.post()).willReturn(requestBodyUriSpec);
            given(requestBodyUriSpec.uri(anyString())).willReturn(requestBodySpec);
            given(requestBodySpec.contentType(any())).willReturn(requestBodySpec);
            given(requestBodySpec.accept(any())).willReturn(requestBodySpec);
            given(requestBodySpec.header(anyString(), anyString())).willReturn(requestBodySpec);
            given(requestBodySpec.body(any())).willReturn(requestBodySpec);
            given(requestBodySpec.retrieve()).willReturn(responseSpec);
            given(responseSpec.body(OcrSpaceResponse.class)).willThrow(new RestClientException("API 호출 실패"));

            // when & then
            assertThatThrownBy(() -> ocrSpaceClientImpl.extractText(image))
                    .isInstanceOf(OcrProcessingException.class);
        }
    }
}