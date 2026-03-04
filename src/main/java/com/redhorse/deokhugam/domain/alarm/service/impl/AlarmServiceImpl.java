package com.redhorse.deokhugam.domain.alarm.service.impl;

import com.redhorse.deokhugam.domain.alarm.dto.NotificationDto;
import com.redhorse.deokhugam.domain.alarm.entity.Alarm;
import com.redhorse.deokhugam.domain.alarm.mapper.AlarmMapper;
import com.redhorse.deokhugam.domain.alarm.repository.AlarmRepository;
import com.redhorse.deokhugam.domain.alarm.service.AlarmService;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import com.redhorse.deokhugam.domain.comment.repository.CommentRepository;
import com.redhorse.deokhugam.domain.review.dto.ReviewLikeDto;
import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.domain.review.repository.ReviewRepository;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {
    private final AlarmRepository alarmRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final AlarmMapper alarmMapper;

    @Override
    public NotificationDto createCommentAlarm(CommentDto dto) {
        // 임시 dto 사용 중

        // 자기 자신을 가져오게 수정
        Optional<User> user = userRepository.findById(dto.userId());
        Optional<User> sender = userRepository.findById(dto.userId());
        Optional<Review> review = reviewRepository.findById(dto.reviewId());

        Alarm alarm = new Alarm(
                "COMMENT",
                dto.content(),
                // 유저랑 댓글 단사람 조합해서 만들기, 나중에 ddl 엔티티 이름 바꿔줘야 할 듯
                dto.userNickName(),
                // 링크의 경우, /api/reviews/{reviewId}가 되게 저장하면 될듯
                // js에서 이렇게 동작하는거 같네,
                // 아니면 그냥 dto.reviewId()만 하면 되나?
//                "/api/reviews/{"+dto.reviewId()+"}",
                dto.reviewId(),
                user.get()
        );

        alarm = alarmRepository.save(alarm);

        // 지금 알림 제목이 문제인데... sender 지우지 말고 이름만 바꿔서 활용해 볼까?
        String title = "[" + sender.get().getNickname() + "]님이 나의 리뷰에 댓글을 남겼습니다.";

        NotificationDto notificationDto = new NotificationDto(
                alarm.getId(),
                alarm.getUser().getId(),
                alarm.getReviewId(),
                title,
                dto.content(),
                false,
                alarm.getCreatedAt(),
                alarm.getUpdatedAt()
        );

        return notificationDto;
    }

    @Override
    public NotificationDto createLikeAlarm(ReviewLikeDto dto) {
        // 임시 dto 사용 중

        // 자기 자신을 가져오게 수정
        Optional<User> user = userRepository.findById(dto.userId());
        Optional<Review> review = reviewRepository.findById(dto.reviewId());

        String content = "[" + user.get().getNickname() + "]님이 나의 리뷰를 좋아합니다.";
        Alarm alarm = new Alarm(
                "LIKE",
                content,
                user.get().getNickname(),
                dto.reviewId(),
                user.get()
        );

        alarm = alarmRepository.save(alarm);

        NotificationDto notificationDto = new NotificationDto(
                alarm.getId(),
                alarm.getUser().getId(),
                alarm.getReviewId(),
                content,
                review.get().getContent(),
                false,
                alarm.getCreatedAt(),
                alarm.getUpdatedAt()
        );

        return notificationDto;
    }

    @Override
    public NotificationDto createDashboardAlarm() {


        return null;
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
