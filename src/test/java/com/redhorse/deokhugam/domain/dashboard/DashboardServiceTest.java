package com.redhorse.deokhugam.domain.dashboard;

import com.redhorse.deokhugam.domain.dashboard.dto.popularbook.PopularBookDto;
import com.redhorse.deokhugam.domain.dashboard.dto.popularreview.PopularReviewDto;
import com.redhorse.deokhugam.domain.dashboard.dto.poweruser.PowerUserDto;
import com.redhorse.deokhugam.domain.dashboard.dto.request.DashboardRequest;
import com.redhorse.deokhugam.domain.dashboard.dto.response.CursorPageResponsePopularBookDto;
import com.redhorse.deokhugam.domain.dashboard.dto.response.CursorPageResponsePopularReviewkDto;
import com.redhorse.deokhugam.domain.dashboard.dto.response.CursorPageResponsePowerUserDto;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularBook;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularReview;
import com.redhorse.deokhugam.domain.dashboard.entity.PowerUser;
import com.redhorse.deokhugam.domain.dashboard.mapper.DashboardMapper;
import com.redhorse.deokhugam.domain.dashboard.repository.PopularBookRepository;
import com.redhorse.deokhugam.domain.dashboard.repository.PopularReviewRepository;
import com.redhorse.deokhugam.domain.dashboard.repository.PowerUserRepository;
import com.redhorse.deokhugam.domain.dashboard.service.DashboardServiceImpl;
import com.redhorse.deokhugam.global.entity.PeriodType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Mock
    private PopularReviewRepository reviewRepository;
    @Mock
    private PowerUserRepository userRepository;
    @Mock
    private PopularBookRepository bookRepository;
    @Mock
    private DashboardMapper dashboardMapper;

    @Test
    @DisplayName("인기 리뷰 조회 - 다음 페이지가 있는 경우 커서 정상 계산")
    void getPopularReviews_WithNextPage() {
        // given: 실제 레코드 객체 생성 (Mock 사용 지양)
        int limit = 2;
        DashboardRequest request = new DashboardRequest(
                PeriodType.ALL_TIME,
                "ASC",
                null,
                null,
                limit
        );

        List<PopularReview> mockList = new ArrayList<>();
        for (int i = 0; i < limit + 1; i++) {
            PopularReview review = mock(PopularReview.class);
            lenient().when(review.getId()).thenReturn(UUID.randomUUID());
            lenient().when(review.getCreatedAt()).thenReturn(Instant.now());
            mockList.add(review);
        }

        given(reviewRepository.getAllPopularReview(any(DashboardRequest.class),  any(LocalDate.class), any(Pageable.class)))
                .willReturn(new SliceImpl<>(mockList));
        given(reviewRepository.countByRequest(request)).willReturn(10L);

        given(dashboardMapper.entityToReviewDto(any(PopularReview.class)))
                .willReturn(mock(PopularReviewDto.class));

        CursorPageResponsePopularReviewkDto response = dashboardService.getPopularReviews(request);

        // then
        assertThat(response.totalElements()).isEqualTo(10L);
        assertThat(response.hasNext()).isTrue();
        assertThat(response.content()).hasSize(limit);
        assertThat(response.nextCursor()).isNotNull();
    }

    @Test
    @DisplayName("파워 유저 조회 - 다음 페이지가 있는 경우 커서 정상 계산")
    void getPowerUsers_WithNextPage() {
        // given
        int limit = 2;
        DashboardRequest request = new DashboardRequest(
                PeriodType.ALL_TIME,
                "DESC",
                null,
                null,
                limit
        );

        List<PowerUser> mockList = new ArrayList<>();
        for (int i = 0; i < limit + 1; i++) {
            PowerUser user = mock(PowerUser.class);
            lenient().when(user.getId()).thenReturn(UUID.randomUUID());
            lenient().when(user.getCreatedAt()).thenReturn(Instant.now());
            mockList.add(user);
        }

        given(userRepository.getAllPowerUser(any(DashboardRequest.class),  any(LocalDate.class), any(Pageable.class)))
                .willReturn(new SliceImpl<>(mockList));
        given(userRepository.count()).willReturn(15L);

        given(dashboardMapper.entityToPowerUserDto(any(PowerUser.class)))
                .willReturn(mock(PowerUserDto.class));

        // when
        CursorPageResponsePowerUserDto response = dashboardService.getPowerUsers(request);

        // then
        assertThat(response.totalElements()).isEqualTo(15L);
        assertThat(response.hasNext()).isTrue();
        assertThat(response.content()).hasSize(limit);
    }

    @Test
    @DisplayName("인기 도서 조회 - 마지막 페이지인 경우 커서 null 반환")
    void getPopularBooks_LastPage() {
        // given
        int limit = 5;
        DashboardRequest request = new DashboardRequest(
                PeriodType.ALL_TIME,
                "ASC",
                null,
                null,
                limit
        );

        List<PopularBook> mockList = new ArrayList<>();
        for (int i = 0; i < 3; i++) { // limit보다 적은 데이터
            PopularBook book = mock(PopularBook.class);
            mockList.add(book);
        }

        given(bookRepository.getAllPopularBook(any(DashboardRequest.class),  any(LocalDate.class), any(Pageable.class)))
                .willReturn(new SliceImpl<>(mockList));
        given(bookRepository.count()).willReturn(3L);

        given(dashboardMapper.entityToBookDto(any(PopularBook.class)))
                .willReturn(mock(PopularBookDto.class));

        // when
        CursorPageResponsePopularBookDto response = dashboardService.getPopularBooks(request);

        // then
        assertThat(response.hasNext()).isFalse();
        assertThat(response.content()).hasSize(3);
        assertThat(response.nextCursor()).isNull();
        assertThat(response.totalElements()).isEqualTo(3L);
    }
}