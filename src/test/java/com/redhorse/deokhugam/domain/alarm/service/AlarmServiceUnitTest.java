package com.redhorse.deokhugam.domain.alarm.service;

import com.redhorse.deokhugam.domain.alarm.dto.NotificationDto;
import com.redhorse.deokhugam.domain.alarm.entity.Alarm;
import com.redhorse.deokhugam.domain.alarm.mapper.AlarmMapper;
import com.redhorse.deokhugam.domain.alarm.repository.AlarmRepository;
import com.redhorse.deokhugam.domain.alarm.service.impl.AlarmServiceImpl;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import com.redhorse.deokhugam.domain.dashboard.dto.popularreview.PopularReviewDto;
import com.redhorse.deokhugam.domain.review.dto.ReviewLikeDto;
import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.domain.review.repository.ReviewRepository;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.exception.UserNotFoundException;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import com.redhorse.deokhugam.global.entity.PeriodType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AlarmServiceUnitTest {

    @InjectMocks
    private AlarmServiceImpl alarmService;

    @Mock
    private AlarmRepository alarmRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AlarmMapper alarmMapper;

    @Test
    @DisplayName("댓글 알림 생성 성공 - 정상적으로 알림이 저장되고 반환된다")
    void createCommentAlarm_Success() {
        // given
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();

        CommentDto dto = new CommentDto(
                UUID.randomUUID(), reviewId, userId, "닉네임",
                "댓글 내용", Instant.now(), Instant.now()
        );

        User mockUser = mock(User.class);
        given(mockUser.getNickname()).willReturn("테스트유저");
        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));

        Alarm mockAlarm = mock(Alarm.class);
        given(alarmRepository.save(any(Alarm.class))).willReturn(mockAlarm);

        NotificationDto expectedDto = mock(NotificationDto.class);
        given(alarmMapper.alarmToNotificationDto(mockAlarm)).willReturn(expectedDto);

        // when
        NotificationDto result = alarmService.createCommentAlarm(dto);

        // then
        assertThat(result).isNotNull();
        verify(alarmRepository).save(any(Alarm.class)); // save 메서드가 1번 호출되었는지 검증
    }

    @Test
    @DisplayName("댓글 알림 생성 실패 - 유저가 존재하지 않으면 UserNotFoundException 발생")
    void createCommentAlarm_UserNotFound() {
        // given
        UUID userId = UUID.randomUUID();

        CommentDto dto = new CommentDto(
                UUID.randomUUID(), UUID.randomUUID(), userId, "닉네임",
                "내용", Instant.now(), Instant.now()
        );

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> alarmService.createCommentAlarm(dto))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("좋아요 알림 생성 성공")
    void createLikeAlarm_Success() {
        // given
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();

        ReviewLikeDto dto = new ReviewLikeDto(reviewId,userId, true);

        User mockUser = mock(User.class);
        given(mockUser.getNickname()).willReturn("테스트유저");

        Review mockReview = mock(Review.class);
        given(mockReview.getContent()).willReturn("리뷰 내용");

        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(mockReview));

        Alarm mockAlarm = mock(Alarm.class);
        given(alarmRepository.save(any(Alarm.class))).willReturn(mockAlarm);
        given(alarmMapper.alarmToNotificationDto(mockAlarm)).willReturn(mock(NotificationDto.class));

        // when
        NotificationDto result = alarmService.createLikeAlarm(dto);

        // then
        assertThat(result).isNotNull();
        verify(alarmRepository).save(any(Alarm.class));
    }

    @Test
    @DisplayName("좋아요 알림 생성 실패 - 리뷰가 존재하지 않으면 예외 발생")
    void createLikeAlarm_ReviewNotFound() {
        // given
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();

        ReviewLikeDto dto = new ReviewLikeDto(reviewId,userId,  true);

        User mockUser = mock(User.class);
        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
        given(reviewRepository.findById(reviewId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> alarmService.createLikeAlarm(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("리뷰가 없습니다.");
    }

    @Test
    @DisplayName("인기 리뷰 알림 생성 성공")
    void createReviewAlarm_Success() {
        // given
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();

        PopularReviewDto dto = new PopularReviewDto(
                UUID.randomUUID(), reviewId, UUID.randomUUID(), "책제목",
                "url", userId, "닉네임", 10,
                4.5, PeriodType.DAILY, Instant.now(), 1, 100.0,
                5, 2
        );

        User mockUser = mock(User.class);
        Review mockReview = mock(Review.class);
        given(mockReview.getContent()).willReturn("리뷰 내용입니다");

        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(mockReview));

        Alarm mockAlarm = mock(Alarm.class);
        given(alarmRepository.save(any(Alarm.class))).willReturn(mockAlarm);
        given(alarmMapper.alarmToNotificationDto(mockAlarm)).willReturn(mock(NotificationDto.class));

        // when
        NotificationDto result = alarmService.createReviewAlarm(dto);

        // then
        assertThat(result).isNotNull();
        verify(alarmRepository).save(any(Alarm.class));
    }
}