package com.redhorse.deokhugam.infra.ocr;

import com.redhorse.deokhugam.infra.ocr.exception.OcrProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;

import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class AwsTextractClientImpl implements OcrClient
{
    private final TextractClient textractClient;

    /**
     * AWS Textract로 이미지에서 텍스트를 추출한다.
     *
     * @param image OCR 처리할 이미지 파일
     * @return 추출된 텍스트
     * @throws OcrProcessingException OCR 처리 중 오류가 발생한 경우
     */
    @Override
    public String extractText(MultipartFile image) {
        try {
            // TextractSDK는 SdkBytes 타입으로 이미지를 받음 (btye[]과 같지만 AWS SDK API의 요구)
            SdkBytes imageBytes = SdkBytes.fromInputStream(image.getInputStream());

            /* 일반 텍스트 추출 */
            DetectDocumentTextRequest request = DetectDocumentTextRequest.builder()
                    .document(Document.builder()
                            .bytes(imageBytes)
                            .build())
                    .build();

            DetectDocumentTextResponse response = textractClient.detectDocumentText(request);

            /* 쿼리 추출 */
//            AnalyzeDocumentRequest request = AnalyzeDocumentRequest.builder()
//                    .document(Document.builder()
//                            .bytes(imageBytes)
//                            .build())
//                    .featureTypes(FeatureType.QUERIES)
//                    .queriesConfig(QueriesConfig.builder()
//                            .queries(Query.builder()
//                                    .text("What is the ISBN numbers?")
//                                    .build())
//                            .build())
//                    .build();
//
//            AnalyzeDocumentResponse response = textractClient.analyzeDocument(request);

//            String text = response.blocks().stream()
//                    .filter(block -> block.blockType() == BlockType.QUERY_RESULT)
//                    .map(Block::text)
//                    .collect(Collectors.joining("\n"));

            String text = response.blocks().stream()
                            .filter(block -> block.blockType() == BlockType.LINE)
                            .map(Block::text)
                            .collect(Collectors.joining("\n"));

            log.info("[Textract-Api] Textract ISBN 추출 작업 완료: fileName={}", image.getOriginalFilename());

            return text;

        } catch (Exception e) {
            log.error("[Textract-Api] Textract ISBN 추출 작업 실패: fileName={}", image.getOriginalFilename(), e);
            throw new OcrProcessingException();
        }
    }
}
