package com.redhorse.deokhugam.infra.naver;

import com.redhorse.deokhugam.infra.naver.dto.NaverBookDto;
import com.redhorse.deokhugam.infra.naver.dto.NaverBookResponse.NaverBookItem;
import com.redhorse.deokhugam.infra.naver.exception.NaverBookNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
@Component
public class NaverBookProvider
{
    private final NaverBookClient naverBookClient;

    private static final int CONNECTION_TIMEOUT_MS = 5000; // 5초
    private static final int READ_TIMEOUT_MS = 10000;      // 10초

    /**
     * ISBN으로 네이버 도서 정보를 조회하여 NaverBooktDto로 반환한다.
     *
     * @param isbn 조회할 도서의 ISBN
     * @return 조회된 도서 정보 (가공후)
     * @throws NaverBookNotFoundException 해당 ISBN의 도서를 찾을 수 없는 경우
     */
    @Cacheable(value = "naverBook", key = "#isbn")
    public NaverBookDto getBookInfoByIsbn(String isbn) {
        NaverBookItem item = naverBookClient.fetchInfoByIsbn(isbn)
                .orElseThrow(() -> new NaverBookNotFoundException(isbn));

        log.info("[Book-Api] 네이버 도서 정보 조회 작업 완료: isbn={}", isbn);

        return new NaverBookDto(
                item.title(),
                item.author(),
                item.publisher(),
                item.description(),
                parseDate(item.pubdate()),
                parseIsbn13(item.isbn()),
                toBase64(item.image())
        );
    }

    /**
     * "yyyyMMdd" 형식의 문자열을 LocalDate로 변환한다.
     *
     * @param pubDate 변환할 날짜 문자열
     * @return 변환된 LocalDate
     */
    private LocalDate parseDate(String pubDate) {
        if (pubDate == null || pubDate.isBlank()) return null;

        return LocalDate.parse(pubDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    /**
     * "ISBN10 ISBN13" 형식에서 ISBN-13을 추출한다.
     *
     * @param isbn 네이버 API에서 변화된 ISBN 문자열
     * @return ISBN-13문자열
     */
    private String parseIsbn13(String isbn) {
        if (isbn == null || isbn.isBlank()) return null;

        for (String part: isbn.split(" ")) {
            if (part.startsWith("978") || part.startsWith("979")) return part;
        }

        return null;
    }

    /**
     * 이미지 URL로부터 이미지를 다운로드하여 Base64로 인코딩한다.
     *
     * @param imageUrl 다운로드할 이미지 URL
     * @return Base64로 인코딩된 이미지 문자열
     */
    private String toBase64(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return null;
        try {
            URLConnection connection = URI.create(imageUrl).toURL().openConnection();
            connection.setConnectTimeout(CONNECTION_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);
            try (InputStream is = connection.getInputStream()) {
                return Base64.getEncoder().encodeToString(is.readAllBytes());
            }
        } catch (Exception e) {
            log.warn("[Naver-Api] 썸네일 다운로드 작업 실패: url={}, error={}", imageUrl, e.getMessage());
            return null;
        }
    }
}
