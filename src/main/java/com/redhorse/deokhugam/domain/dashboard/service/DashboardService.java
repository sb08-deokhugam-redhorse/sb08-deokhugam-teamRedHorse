package com.redhorse.deokhugam.domain.dashboard.service;

import com.redhorse.deokhugam.domain.alarm.dto.CursorPageResponseNotificationDto;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationDto;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationListRequest;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import com.redhorse.deokhugam.domain.dashboard.dto.poweruser.PowerUserDto;
import com.redhorse.deokhugam.domain.dashboard.dto.request.DashboardRequest;
import com.redhorse.deokhugam.domain.dashboard.dto.response.CursorPageResponsePopularBookDto;
import com.redhorse.deokhugam.domain.dashboard.dto.response.CursorPageResponsePopularReviewkDto;
import com.redhorse.deokhugam.domain.dashboard.dto.response.CursorPageResponsePowerUserDto;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularReview;
import com.redhorse.deokhugam.domain.review.dto.ReviewLikeDto;
import org.springframework.cache.annotation.CacheEvict;

import java.util.UUID;

public interface DashboardService {
    CursorPageResponsePopularReviewkDto getPopularReviews(DashboardRequest request);

    CursorPageResponsePowerUserDto getPowerUsers(DashboardRequest request);

    CursorPageResponsePopularBookDto getPopularBooks(DashboardRequest request);

    @CacheEvict(value = "popularReviews", allEntries = true)
    void clearReviewDashboardCache();

    @CacheEvict(value = "powerUsers", allEntries = true)
    void cleaUserDashboardCache();

    @CacheEvict(value = "popularBooks", allEntries = true)
    void clearBooksDashboardCache();
}
