package com.redhorse.deokhugam.infra.ocr;

import com.redhorse.deokhugam.infra.ocr.exception.IsbnNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Component
public class OcrProvider
{
    private final OcrClient ocrClient;

    /**
     * 이미지로부터 ISBN을 추출한다.
     *
     * @param image 추출하고자 하는 이미지
     * @return ISBN
     */
    public String extractIsbn(MultipartFile image) {
        String text = ocrClient.extractText(image);
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
     * <p> 1. 978 또는 979로 시작하는 13자리 숫자
     * <p> 2. "-" 삭제
     * <p> 3. 체크섬 검증으로 유효한 ISBN만 필터링
     *
     * @param text 응답받은 텍스트
     * @return 추출한 텍스트
     */
    private String parseIsbn(String text) {
        Pattern pattern = Pattern.compile("(?:97[89])\\d{10}");
        Matcher matcher = pattern.matcher(text.replaceAll("[\\s\\-]", ""));

        List<String> isbns = new ArrayList<>();
        while (matcher.find()) {
            isbns.add(matcher.group());
        }

        return isbns.stream()
                .filter(this::isValidIsbn)
                .findFirst()
                .orElse(null);
    }

    /**
     * ISBN-13 체크섬을 검증한다.
     *
     * @param isbn 검증할 ISBN-13
     * @return 유효 여부
     */
    private boolean isValidIsbn(String isbn) {
        int sum = 0;
        for (int i=0; i<13; i++) {
            int digit = isbn.charAt(i) - '0';
            sum += (i % 2 == 0) ? digit : digit * 3;
        }

        return sum % 10 == 0;
    }
}
