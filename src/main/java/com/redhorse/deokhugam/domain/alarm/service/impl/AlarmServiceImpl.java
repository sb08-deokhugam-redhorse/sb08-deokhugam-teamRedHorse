package com.redhorse.deokhugam.domain.alarm.service.impl;

import com.redhorse.deokhugam.domain.alarm.dto.NotificationDto;
import com.redhorse.deokhugam.domain.alarm.entity.Alarm;
import com.redhorse.deokhugam.domain.alarm.exception.AlarmNotFoundException;
import com.redhorse.deokhugam.domain.alarm.exception.NoAlarmException;
import com.redhorse.deokhugam.domain.alarm.mapper.AlarmMapper;
import com.redhorse.deokhugam.domain.alarm.repository.AlarmRepository;
import com.redhorse.deokhugam.domain.alarm.service.AlarmService;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import com.redhorse.deokhugam.domain.dashboard.dto.popularreview.PopularReviewDto;
import com.redhorse.deokhugam.domain.review.dto.ReviewLikeDto;
import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.domain.review.repository.ReviewRepository;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.exception.UserNotFoundException;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {
    private final AlarmRepository alarmRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final AlarmMapper alarmMapper;

    @Override
    public NotificationDto createCommentAlarm(CommentDto dto) {
        // 임시 dto 사용 중

        Review review = reviewRepository.findById(dto.reviewId())
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 없습니다."));
        User reviewOwner = review.getUser();
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new UserNotFoundException(dto.userId()));

        Alarm alarm = new Alarm(
                "COMMENT",
                dto.content(),
                "[" + user.getNickname() + "]님이 나의 리뷰에 댓글을 남겼습니다.",
                dto.reviewId(),
                reviewOwner
        );

        alarm = alarmRepository.save(alarm);
        return alarmMapper.alarmToNotificationDto(alarm);
    }

    @Override
    public NotificationDto createLikeAlarm(ReviewLikeDto dto) {
        // 임시 dto 사용 중

        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new UserNotFoundException(dto.userId()));
        Review review = reviewRepository.findById(dto.reviewId())
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 없습니다."));
        User reviewOwner = review.getUser();


        Alarm alarm = new Alarm(
                "LIKE",
                review.getContent(),
                "[" + user.getNickname() + "]님이 나의 리뷰를 좋아합니다.",
                dto.reviewId(),
                reviewOwner
        );

        alarm = alarmRepository.save(alarm);
        return alarmMapper.alarmToNotificationDto(alarm);

    }

    @Override
    public NotificationDto createReviewAlarm(PopularReviewDto dto) {
        // 임시 dto 사용 중

        Review review = reviewRepository.findById(dto.reviewId())
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 없습니다."));
        User reviewOwner = review.getUser();

        String type = "";
        switch (dto.period().toString()) {
            case "DAILY":
                type = "일간 ";
                break;
            case "WEEKLY":
                type = "주간 ";
                break;
            case "MONTHLY":
                type = "월간 ";
                break;
            case "ALL_TIME":
                type = "전체 ";
                break;
        }

        Alarm alarm = new Alarm(
                dto.period().toString(),
                review.getContent(),
                "나의 리뷰가 " + type + dto.rank() + "위에 올랐습니다.",
                dto.reviewId(),
                reviewOwner
        );

        alarm = alarmRepository.save(alarm);
        return alarmMapper.alarmToNotificationDto(alarm);

    }

    @Override
    public void checkAlarm(UUID alarmId, UUID userId) {
        Alarm alarm = alarmRepository.findById(alarmId).orElseThrow(
                () -> new AlarmNotFoundException(alarmId)
        );

        if (alarm.getUser().getId().equals(userId)) {
            alarm.update();
        }
    }

    @Override
    public void checkAllAlarm(UUID userId) {
        List<Alarm> alarms = alarmRepository.findAllAlarmByUserId(userId);

        if (alarms.isEmpty()) {
            throw new NoAlarmException(userId);
        }

        for (Alarm alarm : alarms) {
            alarm.update();
        }
    }

    @Override
    public void deleteAlarm() {
    }

    @Override
    public void getAlarmList() {

    }
}
