package com.redhorse.deokhugam.domain.alarm.service.impl;

import com.redhorse.deokhugam.domain.alarm.dto.CursorPageResponseNotificationDto;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationDto;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationListRequest;
import com.redhorse.deokhugam.domain.alarm.entity.Alarm;
import com.redhorse.deokhugam.domain.alarm.exception.AlarmNotFoundException;
import com.redhorse.deokhugam.domain.alarm.exception.NoAlarmException;
import com.redhorse.deokhugam.domain.alarm.mapper.AlarmMapper;
import com.redhorse.deokhugam.domain.alarm.repository.AlarmRepository;
import com.redhorse.deokhugam.domain.alarm.service.AlarmService;
import com.redhorse.deokhugam.domain.book.entity.Book;
import com.redhorse.deokhugam.domain.book.exception.BookNotFoundException;
import com.redhorse.deokhugam.domain.book.repository.BookRepository;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import com.redhorse.deokhugam.domain.dashboard.dto.popularbook.PopularBookDto;
import com.redhorse.deokhugam.domain.dashboard.dto.popularreview.PopularReviewDto;
import com.redhorse.deokhugam.domain.dashboard.dto.poweruser.PowerUserDto;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularBook;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularReview;
import com.redhorse.deokhugam.domain.review.dto.ReviewLikeDto;
import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.domain.review.exception.ReviewNotFoundException;
import com.redhorse.deokhugam.domain.review.repository.ReviewRepository;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.exception.UserNotFoundException;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {
    private final AlarmRepository alarmRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final AlarmMapper alarmMapper;

    @Override
    public NotificationDto createCommentAlarm(CommentDto dto) {
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
    public NotificationDto createReviewAlarm(PopularReview popularReview) {
        Review review = reviewRepository.findById(popularReview.getReview().getId())
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 없습니다."));
        User reviewOwner = review.getUser();

        String type = "";
        switch (popularReview.getPeriod().toString()) {
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
                popularReview.getPeriod().toString(),
                "나의 리뷰가 " + type + popularReview.getRanking() + "위에 올랐습니다.",
                review.getContent(),
                popularReview.getReview().getId(),
                reviewOwner
        );

        alarm = alarmRepository.save(alarm);
        return alarmMapper.alarmToNotificationDto(alarm);

    }

    @Override
    public NotificationDto createPowerUserAlarm(PowerUserDto dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new UserNotFoundException(dto.userId()));

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
                "",
                user.getNickname()+"님이 유저랭킹" + type + dto.rank() + "위에 올랐습니다.",
                dto.userId(),
                user
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
    public CursorPageResponseNotificationDto getAlarmList(NotificationListRequest request) {

        /**
         * DESC와 ASC가 달라지면 쿼리의 부등호 방향이 달라지기에 쿼리가 2개 필요,
         * 추후에 queryDSL쓰면 해결 가능
         */
        Sort.Direction direction = "ASC".equalsIgnoreCase(request.direction())
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Sort sort = Sort.by(direction, "createdAt").and(Sort.by(direction, "id"));

        Pageable pageable = PageRequest.of(0, request.limit() + 1, sort);

        Slice<Alarm> alarmSlice = "ASC".equalsIgnoreCase(request.direction())
                ? alarmRepository.getAllAlarmsAsc(request, pageable)
                : alarmRepository.getAllAlarmsDesc(request, pageable);

        List<Alarm> alarmList = alarmSlice.getContent();
        Long alarmCount = alarmRepository.countAlarmsByUserId(request.userId());

        String nextCursor = null;
        Instant nextAfter = null;
        boolean hasNext = alarmList.size() > request.limit();

        if (hasNext) {
            Alarm last = alarmList.get(request.limit() - 1);
            nextCursor = last.getId().toString();
            nextAfter = last.getCreatedAt();
            alarmList = alarmList.subList(0, request.limit());
        }

        List<NotificationDto> content = alarmList.stream()
                .map(alarmMapper::alarmToNotificationDto)
                .toList();

        return new CursorPageResponseNotificationDto(
                content,
                nextCursor,
                nextAfter,
                content.size(),
                alarmCount,
                hasNext
        );
    }
}
