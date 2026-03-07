package com.redhorse.deokhugam.domain.alarm.service;

import com.redhorse.deokhugam.domain.alarm.dto.CursorPageResponseNotificationDto;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationDto;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationListRequest;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import com.redhorse.deokhugam.domain.dashboard.dto.popularreview.PopularReviewDto;
import com.redhorse.deokhugam.domain.review.dto.ReviewLikeDto;

import java.util.UUID;

public interface AlarmService {
    NotificationDto createCommentAlarm(CommentDto dto);

    NotificationDto createLikeAlarm(ReviewLikeDto dto);

    NotificationDto createReviewAlarm(PopularReviewDto dto);

    void checkAlarm(UUID alarmId, UUID userId);

    void checkAllAlarm(UUID userId);

    CursorPageResponseNotificationDto getAlarmList(NotificationListRequest request);
}
