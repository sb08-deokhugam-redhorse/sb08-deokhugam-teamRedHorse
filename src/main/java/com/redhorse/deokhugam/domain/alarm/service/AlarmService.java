package com.redhorse.deokhugam.domain.alarm.service;

import com.redhorse.deokhugam.domain.alarm.dto.CursorPageResponseNotificationDto;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationDto;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationListRequest;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import com.redhorse.deokhugam.domain.dashboard.dto.poweruser.PowerUserDto;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularReview;
import com.redhorse.deokhugam.domain.review.dto.ReviewLikeDto;

import java.util.UUID;

public interface AlarmService {
    NotificationDto createCommentAlarm(CommentDto dto);

    NotificationDto createLikeAlarm(ReviewLikeDto dto);

    NotificationDto createReviewAlarm(PopularReview popularReview);
    NotificationDto createPowerUserAlarm(PowerUserDto dto);

    void checkAlarm(UUID alarmId, UUID userId);

    void checkAllAlarm(UUID userId);

    CursorPageResponseNotificationDto getAlarmList(NotificationListRequest request);
}
