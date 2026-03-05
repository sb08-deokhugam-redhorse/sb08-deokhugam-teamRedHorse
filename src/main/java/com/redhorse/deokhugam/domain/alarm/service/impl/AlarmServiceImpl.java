package com.redhorse.deokhugam.domain.alarm.service.impl;

import com.redhorse.deokhugam.domain.alarm.dto.NotificationDto;
import com.redhorse.deokhugam.domain.alarm.entity.Alarm;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {
    private final AlarmRepository alarmRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final AlarmMapper alarmMapper;

    @Override
    public NotificationDto createCommentAlarm(CommentDto dto) {
        // 임시 dto 사용 중

        Optional<Review> review = reviewRepository.findById(dto.reviewId());
        User reviewOwner = review.get().getUser();
        Optional<User> user = userRepository.findById(dto.userId());

        Alarm alarm = new Alarm(
                "COMMENT",
                dto.content(),
                "[" + user.get().getNickname() + "]님이 나의 리뷰에 댓글을 남겼습니다.",
                dto.reviewId(),
                reviewOwner
        );

        alarm = alarmRepository.save(alarm);
        return alarmMapper.alarmToNotificationDto(alarm);
    }

    @Override
    public NotificationDto createLikeAlarm(ReviewLikeDto dto) {
        // 임시 dto 사용 중

        Optional<Review> review = reviewRepository.findById(dto.reviewId());
        User reviewOwner = review.get().getUser();
        Optional<User> user = userRepository.findById(dto.userId());

        Alarm alarm = new Alarm(
                "LIKE",
                review.get().getContent(),
                "[" + user.get().getNickname() + "]님이 나의 리뷰를 좋아합니다.",
                dto.reviewId(),
                reviewOwner
        );

        alarm = alarmRepository.save(alarm);
        return alarmMapper.alarmToNotificationDto(alarm);

    }

    @Override
    public NotificationDto createReviewAlarm(PopularReviewDto dto) {
        // 임시 dto 사용 중

        Optional<User> user = userRepository.findById(dto.userId());
        Optional<Review> review = reviewRepository.findById(dto.reviewId());
        User reviewOwner = review.get().getUser();

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
                review.get().getContent(),
                "나의 리뷰가 " + type + dto.rank() + "위에 올랐습니다.",
                dto.reviewId(),
                reviewOwner
        );

        alarm = alarmRepository.save(alarm);
        return alarmMapper.alarmToNotificationDto(alarm);

    }

    @Override
    public void checkAlarm() {

    }

    @Override
    public void checkAllAlarm() {

    }

    @Override
    public void deleteAlarm() {
    }

    @Override
    public void getAlarmList() {

    }
}
