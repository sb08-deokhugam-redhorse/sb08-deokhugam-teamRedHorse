package com.redhorse.deokhugam.infra.ocr;

import com.redhorse.deokhugam.infra.ocr.dto.OcrSpaceResponse;
import com.redhorse.deokhugam.infra.ocr.exception.ImageSizeExceededException;
import com.redhorse.deokhugam.infra.ocr.exception.OcrProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

@Slf4j
@Component
public class OcrSpaceClientImpl implements OcrClient
{
    @Value("${ocr.space.api-key}")
    private String ocrApiKey;

    @Value("${ocr.space.url}")
    private String ocrUrl;

    private final RestClient restClient;

    private static final long MAX_FILE_SIZE = 1024 * 1024; // 1MB

    public OcrSpaceClientImpl(RestClient.Builder restClientBuilder) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(3000)); // 3s
        requestFactory.setReadTimeout(Duration.ofMillis(5000));    // 5s

        this.restClient = restClientBuilder.requestFactory(requestFactory).build();
    }

    /**
     * 이미지에서 텍스트를 추출하여 반환한다.
     *
     * @param image OCR 처리할 이미지 파일
     * @return 추출된 텍스트
     */
    @Override
    public String extractText(MultipartFile image) {
        if (image.getSize() > MAX_FILE_SIZE) {
            throw new ImageSizeExceededException(image.getSize());
        }

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("language", "eng");
        body.add("OCREngine", "2");
        body.add("scale", "true");             // 저해상도
        body.add("detectOrientation", "true"); // 이미지 방향
        body.add("isOverlayRequired", "false");
        body.add("file", image.getResource());

        try {
            OcrSpaceResponse response = restClient.post()
                    .uri(ocrUrl)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("apikey", ocrApiKey)
                    .body(body)
                    .retrieve()
                    .body(OcrSpaceResponse.class);

            if (response == null || response.isErroredOnProcessing() || response.parsedResults() == null || response.parsedResults().isEmpty()) {
                log.error("[OCR-Api] OCR 응답 내용: {}", response);
                throw new OcrProcessingException();
            }

            log.info("[OCR-Api] OCR 작업 완료: fileName={}", image.getOriginalFilename());

            return response.parsedResults().get(0).parsedText();
        } catch (OcrProcessingException e) {
            throw e;
        } catch (Exception e) {
            log.error("[OCR-Api] OCR 작업 실패: fileName={}, error={}", image.getOriginalFilename(), e.getMessage());
            throw new OcrProcessingException(e);
        }
    }
}
