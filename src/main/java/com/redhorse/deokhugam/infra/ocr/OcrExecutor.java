package com.redhorse.deokhugam.infra.ocr;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Component
public class OcrExecutor
{
    private final OcrSpaceClientImpl ocrSpaceClientImpl;
    private final AwsTextractClientImpl awsTextractClientImpl;

    /* "빠른 폴백" VS "최대한 재시도" */

    // CB외, Retry내: 재시도 먼저 -> 실패 -> CB 집계
    // "최대한 시도해보고 안되면 차단" -> 일시적 장애에 강함
    // 표준 패턴

    // Retry외, CB내: CB가 OPEN이면 재시도 자체를 반복
    // "CB 상태와 무관하게 계속 시도" -> 빠른 차단보다 지속 시도가 중요할 때
    // 거의 안 씀

    // deokhugam? -> 빠르게 Textract로 넘어가는 게 목적

    @CircuitBreaker(name = "ocrSpace", fallbackMethod = "extractWithTextract")
    @Retry(name = "ocrSpace")
    public String extractText(MultipartFile image) {
            return ocrSpaceClientImpl.extractText(image);
    }

    private String extractWithTextract(MultipartFile image, Throwable t) {
        log.warn("[OCR-Api] OCR 작업 실패, Textract 폴백: fileName={}, reason={}",
                image.getOriginalFilename(), t.getMessage());

        return awsTextractClientImpl.extractText(image);
    }
}
