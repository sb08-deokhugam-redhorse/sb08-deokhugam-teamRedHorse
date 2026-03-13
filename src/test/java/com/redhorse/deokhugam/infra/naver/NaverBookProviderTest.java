package com.redhorse.deokhugam.infra.naver;

import com.redhorse.deokhugam.infra.naver.dto.NaverBookDto;
import com.redhorse.deokhugam.infra.naver.dto.NaverBookResponse.NaverBookItem;
import com.redhorse.deokhugam.infra.naver.exception.NaverBookNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("NaverBookProvider Unit Test")
class NaverBookProviderTest
{
    private NaverBookProvider naverBookProvider;
    @Mock private NaverBookClient naverBookClient;

    @BeforeEach
    void setUp() {
        naverBookProvider = new NaverBookProvider(naverBookClient, 3000, 1000);
    }

    private NaverBookItem createNaverBookItem() {
        return new NaverBookItem(
                "자바의 정석", "남궁성", "도우출판", "자바 기본서",
                "20160101", "8994492038 9788994492032",
                "https://thumbnail.url"
        );
    }

    @Nested
    @DisplayName("도서 정보 획득")
    class Get {
        @Test
        @DisplayName("성공 - ISBN으로 도서 정보를 반환한다")
        void success_withValidIsbn_returnsNaverBookDto() {
            given(naverBookClient.fetchInfoByIsbn("9788994492032"))
                    .willReturn(Optional.of(createNaverBookItem()));

            NaverBookDto result = naverBookProvider.getBookInfoByIsbn("9788994492032");

            assertThat(result.title()).isEqualTo("자바의 정석");
            assertThat(result.author()).isEqualTo("남궁성");
            assertThat(result.publishedDate()).isEqualTo(LocalDate.of(2016, 1, 1));
            assertThat(result.isbn()).isEqualTo("9788994492032");
        }

        @Test
        @DisplayName("성공 - isbn에서 ISBN-13을 추출한다")
        void success_extractsIsbn13FromIsbnField() {
            given(naverBookClient.fetchInfoByIsbn("9788994492032"))
                    .willReturn(Optional.of(createNaverBookItem()));

            NaverBookDto result = naverBookProvider.getBookInfoByIsbn("9788994492032");

            assertThat(result.isbn()).isEqualTo("9788994492032");
        }

        @Test
        @DisplayName("실패 - 도서를 찾을 수 없으면 NaverBookNotFoundException을 던진다")
        void fail_withNoResult_throwsNaverBookNotFoundException() {
            given(naverBookClient.fetchInfoByIsbn("9788994492032"))
                    .willReturn(Optional.empty());

            assertThatThrownBy(() -> naverBookProvider.getBookInfoByIsbn("9788994492032"))
                    .isInstanceOf(NaverBookNotFoundException.class);
        }
    }
}