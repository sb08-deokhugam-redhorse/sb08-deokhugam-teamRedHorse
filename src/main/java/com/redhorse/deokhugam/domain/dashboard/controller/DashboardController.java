package com.redhorse.deokhugam.domain.dashboard.controller;


import com.redhorse.deokhugam.domain.dashboard.controller.docs.DashboardApi;
import com.redhorse.deokhugam.domain.dashboard.dto.request.DashboardRequest;
import com.redhorse.deokhugam.domain.dashboard.dto.response.CursorPageResponsePopularBookDto;
import com.redhorse.deokhugam.domain.dashboard.dto.response.CursorPageResponsePopularReviewkDto;
import com.redhorse.deokhugam.domain.dashboard.dto.response.CursorPageResponsePowerUserDto;
import com.redhorse.deokhugam.domain.dashboard.service.DashboardService;
import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class DashboardController implements DashboardApi {
    private final DashboardService dashboardService;

    @Override
    @GetMapping("/reviews/popular") // 대시보드용 인기 리뷰
    public ResponseEntity<CursorPageResponsePopularReviewkDto> getPopularReviews(DashboardRequest request) {
       return ResponseEntity
               .status(HttpStatus.OK)
               .body(dashboardService.getPopularReviews(request));
    }

    @Override
    @GetMapping("/users/power") // 대시보드용 파워 유저
    public ResponseEntity<CursorPageResponsePowerUserDto> getPowerUsers(DashboardRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dashboardService.getPowerUsers(request));
    }


    @Override
    @GetMapping("/books/popular") // 대시보드용 인기 도서
    public ResponseEntity<CursorPageResponsePopularBookDto> getPopularBooks(DashboardRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dashboardService.getPopularBooks(request));
    }
}
