package com.redhorse.deokhugam.infra.naver;

import com.redhorse.deokhugam.domain.book.exception.InValidIsbnException;
import com.redhorse.deokhugam.infra.naver.dto.NaverBookResponse;
import com.redhorse.deokhugam.infra.naver.dto.NaverBookResponse.NaverBookItem;
import com.redhorse.deokhugam.infra.naver.exception.NaverApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Component
public class NaverBookClientImpl implements NaverBookClient
{
    private final String clientId;
    private final String clientSecret;
    private final String naverUrl;
    private final RestClient restClient;

    public NaverBookClientImpl(RestClient.Builder restClientBuilder,
                               @Value("${naver.api.client-id}") String clientId,
                               @Value("${naver.api.client-secret}") String clientSecret,
                               @Value("${naver.api.url}") String naverUrl,
                               @Value("${naver.api.connect-timeout}") int connectTimeout,
                               @Value("${naver.api.read-timeout}") int readTimeout)
    {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.naverUrl = naverUrl;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(connectTimeout)); // 3s
        requestFactory.setReadTimeout(Duration.ofMillis(readTimeout));       // 1s

        this.restClient = restClientBuilder.requestFactory(requestFactory).build();
    }

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
            throw new NaverApiException(isbn);
        }
    }
}
