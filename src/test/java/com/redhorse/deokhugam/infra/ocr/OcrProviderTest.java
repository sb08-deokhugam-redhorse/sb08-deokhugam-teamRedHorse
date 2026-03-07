package com.redhorse.deokhugam.infra.ocr;

import com.redhorse.deokhugam.infra.ocr.exception.IsbnNotFoundException;
import com.redhorse.deokhugam.infra.ocr.exception.OcrProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OcrProviderTest
{
    @InjectMocks private OcrProvider ocrProvider;
    @Mock private OcrSpaceClientImpl ocrSpaceClient;

    private MockMultipartFile createMockImage() {
        return new MockMultipartFile("image", "test.png", "image/png", new byte[100]);
    }

    @Nested
    @DisplayName("ISBN 추출")
    class Extract {
        @Test
        @DisplayName("성공 - 텍스트에서 ISBN-13을 추출한다")
        void success_withValidText_returnsIsbn() {
            given(ocrSpaceClient.extractText(any())).willReturn("ISBN 9788994492032");

            String result = ocrProvider.extractIsbn(createMockImage());

            assertThat(result).isEqualTo("9788994492032");
        }

        @Test
        @DisplayName("성공 - \"-\" 이 포함된 ISBN도 추출한다")
        void success_withHyphenatedIsbn_returnsIsbn() {
            given(ocrSpaceClient.extractText(any())).willReturn("ISBN 978-89-944920-3-2");

            String result = ocrProvider.extractIsbn(createMockImage());

            assertThat(result).isEqualTo("9788994492032");
        }

        @Test
        @DisplayName("성공 - OCR 오인식 보정 후 ISBN을 추출한다")
        void success_withOcrMisrecognition_returnsIsbn() {
            given(ocrSpaceClient.extractText(any())).willReturn("ISBN 978899449203l"); // l → 1

            String result = ocrProvider.extractIsbn(createMockImage());

            assertThat(result).isEqualTo("9788994492031");
        }

        @Test
        @DisplayName("실패 - ISBN을 찾을 수 없으면 IsbnNotFoundException을 던진다")
        void fail_withNoIsbn_throwsIsbnNotFoundException() {
            given(ocrSpaceClient.extractText(any())).willReturn("이 텍스트에는 ISBN이 없습니다");

            assertThatThrownBy(() -> ocrProvider.extractIsbn(createMockImage()))
                    .isInstanceOf(IsbnNotFoundException.class);
        }

        @Test
        @DisplayName("실패 - OCR 처리 실패 시 OcrProcessingException을 전파한다")
        void fail_withOcrFailure_propagatesOcrProcessingException() {
            given(ocrSpaceClient.extractText(any())).willThrow(new OcrProcessingException());

            assertThatThrownBy(() -> ocrProvider.extractIsbn(createMockImage()))
                    .isInstanceOf(OcrProcessingException.class);
        }
    }
}