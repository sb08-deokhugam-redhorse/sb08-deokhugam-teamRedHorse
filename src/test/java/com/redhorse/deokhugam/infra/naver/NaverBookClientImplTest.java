package com.redhorse.deokhugam.infra.naver;

import com.redhorse.deokhugam.domain.book.exception.InValidIsbnException;
import com.redhorse.deokhugam.infra.naver.dto.NaverBookResponse;
import com.redhorse.deokhugam.infra.naver.dto.NaverBookResponse.NaverBookItem;
import com.redhorse.deokhugam.infra.naver.exception.NaverApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("NaverBookClient Unit Test")
class NaverBookClientImplTest
{
    @Mock private RestClient.Builder restClientBuilder;
    @Mock private RestClient restClient;
    @Mock private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private RestClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private RestClient.ResponseSpec responseSpec;

    private NaverBookClientImpl naverBookClientImpl;

    @BeforeEach
    void setUp() {
        given(restClientBuilder.requestFactory(any())).willReturn(restClientBuilder);
        given(restClientBuilder.build()).willReturn(restClient);

        naverBookClientImpl = new NaverBookClientImpl(restClientBuilder);

        ReflectionTestUtils.setField(naverBookClientImpl, "clientId", "test-client-id");
        ReflectionTestUtils.setField(naverBookClientImpl, "clientSecret", "test-client-secret");
        ReflectionTestUtils.setField(naverBookClientImpl, "naverUrl", "https://openapi.naver.com/v1/search/book.json?query=");
    }

    @Nested
    @DisplayName("Naver API 호출")
    class Fetch {
        @Test
        @DisplayName("성공 - 유효한 ISBN으로 도서 정보를 반환한다")
        void success_withValidIsbn_returnsBookItem() {
            // given
            String isbn = "9791234567890";
            NaverBookResponse.NaverBookItem item = new NaverBookResponse.NaverBookItem(
                    "클린코드", "마틴", "좋은 코드", "인사이트", "20200101", isbn, "http://image.url"
            );

            NaverBookResponse response = new NaverBookResponse(List.of(item));

            given(restClient.get()).willReturn(requestHeadersUriSpec);
            given(requestHeadersUriSpec.uri(anyString())).willReturn(requestHeadersSpec);
            given(requestHeadersSpec.header(anyString(), anyString())).willReturn(requestHeadersSpec);
            given(requestHeadersSpec.retrieve()).willReturn(responseSpec);
            given(responseSpec.body(NaverBookResponse.class)).willReturn(response);

            // when
            Optional<NaverBookResponse.NaverBookItem> result = naverBookClientImpl.fetchInfoByIsbn(isbn);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().title()).isEqualTo("클린코드");
            assertThat(result.get().isbn()).isEqualTo(isbn);
        }

        @Test
        @DisplayName("성공 - 검색 결과가 없으면 Optional.empty를 반환한다")
        void success_withNoResult_returnsEmpty() {
            given(restClient.get()).willReturn(requestHeadersUriSpec);
            given(requestHeadersUriSpec.uri(anyString())).willReturn(requestHeadersSpec);
            given(requestHeadersSpec.header(anyString(), anyString())).willReturn(requestHeadersSpec);
            given(requestHeadersSpec.retrieve()).willReturn(responseSpec);
            given(responseSpec.body(NaverBookResponse.class))
                    .willReturn(new NaverBookResponse(List.of()));

            Optional<NaverBookItem> result = naverBookClientImpl.fetchInfoByIsbn("9788994492032");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("실패 - ISBN이 null이면 InvalidIsbnException을 던진다")
        void fail_withNullIsbn_throwsException() {
            assertThatThrownBy(() -> naverBookClientImpl.fetchInfoByIsbn(null))
                    .isInstanceOf(InValidIsbnException.class);
        }

        @Test
        @DisplayName("실패 - API 호출 실패 시 NaverApiException을 던진다")
        void fail_withApiError_throwsNaverApiException() {
            given(restClient.get()).willReturn(requestHeadersUriSpec);
            given(requestHeadersUriSpec.uri(anyString())).willReturn(requestHeadersSpec);
            given(requestHeadersSpec.header(anyString(), anyString())).willReturn(requestHeadersSpec);
            given(requestHeadersSpec.retrieve()).willReturn(responseSpec);
            given(responseSpec.body(NaverBookResponse.class))
                    .willThrow(new RestClientException("API Error"));

            assertThatThrownBy(() -> naverBookClientImpl.fetchInfoByIsbn("9788994492032"))
                    .isInstanceOf(NaverApiException.class);
        }
    }
}