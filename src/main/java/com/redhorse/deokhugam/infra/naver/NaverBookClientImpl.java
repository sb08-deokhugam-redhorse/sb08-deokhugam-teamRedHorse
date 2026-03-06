package com.redhorse.deokhugam.infra.naver;

import com.redhorse.deokhugam.domain.book.exception.InValidIsbnException;
import com.redhorse.deokhugam.domain.book.exception.NaverApiException;
import com.redhorse.deokhugam.infra.naver.dto.NaverBookResponse;
import com.redhorse.deokhugam.infra.naver.dto.NaverBookResponse.NaverBookItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class NaverBookClientImpl implements NaverBookClient
{
    @Value("${naver.api.client-id}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;

    @Value("${naver.api.url}")
    private String naverUrl;

    private final RestClient restClient;

    /**
     * ISBN으로 네이버에 API 요청을 보낸다.
     *
     * @param isbn 조회할 ISBN
     * @return 네이버 API로부터 받은 도서 정보 (가공전)
     */
    @Override
    public Optional<NaverBookItem> fetchInfoByIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            throw new InValidIsbnException(isbn);
        }

        try {
            NaverBookResponse response = restClient.get()
                    .uri(naverUrl + isbn)
                    .header("X-Naver-Client-Id", clientId)
                    .header("X-Naver-Client-Secret", clientSecret)
                    .retrieve()
                    .body(NaverBookResponse.class);

            log.info("[Book-Api] 네이버 API 작업 완료: isbn={}", isbn);

            if (response == null || response.items() == null || response.items().isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(response.items().get(0));
        } catch (RestClientException e) {
            log.error("[Book-Api] 네이버 API 호출 실패: isbn={}", isbn);
            throw new NaverApiException(e);
        }
    }
}
