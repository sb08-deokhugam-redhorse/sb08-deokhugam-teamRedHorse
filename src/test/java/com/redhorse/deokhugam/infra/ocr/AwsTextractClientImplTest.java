package com.redhorse.deokhugam.infra.ocr;

import com.redhorse.deokhugam.infra.ocr.exception.OcrProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("AwsTextractClientImpl Unit Test")
class AwsTextractClientImplTest
{
    @Mock private TextractClient textractClient;

    private AwsTextractClientImpl awsTextractClientImpl;

    @BeforeEach
    void setUp() {
        awsTextractClientImpl = new AwsTextractClientImpl(textractClient);
    }

    private MockMultipartFile mockMultipartFile() {
        return new MockMultipartFile("file", "test", "text/plain", "test".getBytes());
    }

    @Nested
    @DisplayName("텍스트 추출")
    class ExtractText {
        @Test
        @DisplayName("성공 - 이미지에서 텍스트를 추출한다")
        void success_withValidImage_returnsText() {
            Block lineBlock = Block.builder()
                    .blockType(BlockType.LINE)
                    .text("ISBN 9788994492032")
                    .build();
            Block wordBlock = Block.builder()
                    .blockType(BlockType.WORD)
                    .text("ISBN")
                    .build();

            given(textractClient.detectDocumentText(any(DetectDocumentTextRequest.class)))
                    .willReturn(DetectDocumentTextResponse.builder()
                            .blocks(lineBlock, wordBlock)
                            .build());

            String result = awsTextractClientImpl.extractText(mockMultipartFile());

            assertThat(result).isEqualTo("ISBN 9788994492032");
        }

        @Test
        @DisplayName("성공 - LINE 블록이 여러 개면 줄바꿈으로 합친다")
        void success_withMultipleLines_returnsJoinedText() {
            Block line1 = Block.builder()
                    .blockType(BlockType.LINE)
                    .text("자바의 정석")
                    .build();
            Block line2 = Block.builder()
                    .blockType(BlockType.LINE)
                    .text("ISBN 9788994492032")
                    .build();

            given(textractClient.detectDocumentText(any(DetectDocumentTextRequest.class)))
                    .willReturn(DetectDocumentTextResponse.builder()
                            .blocks(line1, line2)
                            .build());

            String result = awsTextractClientImpl.extractText(mockMultipartFile());

            assertThat(result).isEqualTo("자바의 정석\nISBN 9788994492032");
        }

        @Test
        @DisplayName("실패 - Textract API 호출 실패 시 OcrProcessingException을 던진다")
        void fail_withApiError_throwsOcrProcessingException() {
            given(textractClient.detectDocumentText(any(DetectDocumentTextRequest.class)))
                    .willThrow(TextractException.builder().message("API 호출 실패").build());

            assertThatThrownBy(() -> awsTextractClientImpl.extractText(mockMultipartFile()))
                    .isInstanceOf(OcrProcessingException.class);
        }
    }
}