package com.redhorse.deokhugam.domain.dashboard;

import com.redhorse.deokhugam.domain.dashboard.controller.DashboardController;
import com.redhorse.deokhugam.domain.dashboard.dto.request.DashboardRequest;
import com.redhorse.deokhugam.domain.dashboard.dto.response.CursorPageResponsePopularBookDto;
import com.redhorse.deokhugam.domain.dashboard.dto.response.CursorPageResponsePopularReviewkDto;
import com.redhorse.deokhugam.domain.dashboard.dto.response.CursorPageResponsePowerUserDto;
import com.redhorse.deokhugam.domain.dashboard.service.DashboardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@ActiveProfiles("test")
public class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    UUID userId = UUID.randomUUID();

    @Test
    @DisplayName("대시보드 인기 리뷰 조회 성공")
    void getPopularReviews_Success() throws Exception {
        // given
        CursorPageResponsePopularReviewkDto mockResponse = mock(CursorPageResponsePopularReviewkDto.class);
        given(dashboardService.getPopularReviews(any(DashboardRequest.class))).willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/reviews/popular")
                        .header("Deokhugam-Request-User-ID", userId)
                        .param("period", "WEEKLY")
                        .param("direction", "DESC") 
                        .param("limit", "10")
                        .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("대시보드 파워 유저 조회 성공")
    void getPowerUsers_Success() throws Exception {
        // given
        CursorPageResponsePowerUserDto mockResponse = mock(CursorPageResponsePowerUserDto.class);
        given(dashboardService.getPowerUsers(any(DashboardRequest.class))).willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/users/power")
                        .header("Deokhugam-Request-User-ID", userId)
                        .param("period", "MONTHLY")
                        .param("direction", "DESC") 
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("대시보드 인기 도서 조회 성공")
    void getPopularBooks_Success() throws Exception {
        // given
        CursorPageResponsePopularBookDto mockResponse = mock(CursorPageResponsePopularBookDto.class);
        given(dashboardService.getPopularBooks(any(DashboardRequest.class))).willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/books/popular")
                        .header("Deokhugam-Request-User-ID", userId)
                        .param("period", "DAILY")
                        .param("direction", "DESC") 
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}