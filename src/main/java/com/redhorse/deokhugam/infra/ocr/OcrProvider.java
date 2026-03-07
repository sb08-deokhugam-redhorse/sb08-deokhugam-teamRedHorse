package com.redhorse.deokhugam.infra.ocr;

import com.redhorse.deokhugam.infra.ocr.exception.IsbnNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Component
public class OcrProvider
{
    private final OcrSpaceClientImpl ocrSpaceClient;

    public String extractIsbn(MultipartFile image) {
        String text = ocrSpaceClient.extractText(image);

        String isbn = parseIsbn(normalize(text));

        if (isbn == null) {
            throw new IsbnNotFoundException();
        }

        log.info("[OCR-Api] ISBN 추출 작업 완료: isbn={}", isbn);

        return isbn;
    }

    /**
     * OCR 오인식 보정
     *
     * @param text 응답받은 텍스트
     * @return 보정받은 텍스트
     */
    private String normalize(String text) {
        return text.replace("O", "0")
                .replace("l", "1")
                .replace("I", "1");
    }

    /**
     * 텍스트에서 ISBN-13을 추출한다.
     * <p>
     *     1. 978 또는 979로 시작하는 13자리 숫자
     *     2. "-" 삭제
     * </p>
     *
     * @param text 응답받은 텍스트
     * @return 추출한 텍스트
     */
    private String parseIsbn(String text) {
        Pattern pattern = Pattern.compile("(?:97[89])\\d{10}");
        Matcher matcher = pattern.matcher(text.replaceAll("[\\s\\-]", ""));
        return matcher.find() ? matcher.group() : null;
    }
}
