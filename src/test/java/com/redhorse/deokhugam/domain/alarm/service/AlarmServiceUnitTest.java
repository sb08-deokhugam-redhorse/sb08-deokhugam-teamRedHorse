package com.redhorse.deokhugam.domain.alarm.service;

import com.redhorse.deokhugam.domain.alarm.dto.NotificationDto;
import com.redhorse.deokhugam.domain.alarm.entity.Alarm;
import com.redhorse.deokhugam.domain.alarm.mapper.AlarmMapper;
import com.redhorse.deokhugam.domain.alarm.repository.AlarmRepository;
import com.redhorse.deokhugam.domain.alarm.service.impl.AlarmServiceImpl;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularReview;
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
    @DisplayName("댓글 알림 생성 성공 - 리뷰 작성자에게 알림이 저장된다")
    void createCommentAlarm_Success() {
        // given
        UUID testUserId = UUID.randomUUID();
        UUID testReviewId = UUID.randomUUID();

        CommentDto dto = new CommentDto(
                UUID.randomUUID(), testReviewId, testUserId, "닉네임",
                "댓글 내용", Instant.now(), Instant.now()
        );

        Review mockReview = mock(Review.class);
        User mockReviewOwner = mock(User.class);
        given(mockReview.getUser()).willReturn(mockReviewOwner);
        given(reviewRepository.findById(testReviewId)).willReturn(Optional.of(mockReview));

        User mockCommenter = mock(User.class);
        given(mockCommenter.getNickname()).willReturn("테스트유저");
        given(userRepository.findById(testUserId)).willReturn(Optional.of(mockCommenter));

        Alarm mockAlarm = mock(Alarm.class);
        given(alarmRepository.save(any(Alarm.class))).willReturn(mockAlarm);

        NotificationDto expectedDto = mock(NotificationDto.class);
        given(alarmMapper.alarmToNotificationDto(mockAlarm)).willReturn(expectedDto);

        // when
        NotificationDto result = alarmService.createCommentAlarm(dto);

        // then
        assertThat(result).isNotNull();
        verify(alarmRepository).save(any(Alarm.class));
    }

    @Test
    @DisplayName("댓글 알림 생성 실패 - 리뷰가 존재하지 않으면 예외 발생")
    void createCommentAlarm_ReviewNotFound() {
        // given
        UUID testUserId = UUID.randomUUID();
        UUID testReviewId = UUID.randomUUID();

        CommentDto dto = new CommentDto(
                UUID.randomUUID(), testReviewId, testUserId, "닉네임",
                "내용", Instant.now(), Instant.now()
        );

        given(reviewRepository.findById(testReviewId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> alarmService.createCommentAlarm(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("리뷰가 없습니다.");
    }

    @Test
    @DisplayName("좋아요 알림 생성 성공")
    void createLikeAlarm_Success() {
        // given
        UUID testUserId = UUID.randomUUID();
        UUID testReviewId = UUID.randomUUID();

        ReviewLikeDto dto = new ReviewLikeDto(testReviewId, testUserId, true);

        User mockLiker = mock(User.class);
        given(mockLiker.getNickname()).willReturn("테스트유저");
        given(userRepository.findById(testUserId)).willReturn(Optional.of(mockLiker));

        Review mockReview = mock(Review.class);
        User mockReviewOwner = mock(User.class);
        given(mockReview.getUser()).willReturn(mockReviewOwner);
        given(mockReview.getContent()).willReturn("리뷰 내용");
        given(reviewRepository.findById(testReviewId)).willReturn(Optional.of(mockReview));

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
    @DisplayName("좋아요 알림 생성 실패 - 유저가 없으면 예외 발생")
    void createLikeAlarm_UserNotFound() {
        // given
        UUID testUserId = UUID.randomUUID();
        UUID testReviewId = UUID.randomUUID();

        ReviewLikeDto dto = new ReviewLikeDto(testReviewId, testUserId, true);

        given(userRepository.findById(testUserId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> alarmService.createLikeAlarm(dto))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("인기 리뷰 알림 생성 성공")
    void createReviewAlarm_Success() {
        // given
        UUID testReviewId = UUID.randomUUID();

        // 1. PopularReview 내부에 들어갈 Review 객체 모킹
        Review mockReview = mock(Review.class);
        given(mockReview.getId()).willReturn(testReviewId); // popularReview.getReview().getId()를 위해 필요

        User mockReviewOwner = mock(User.class);
        given(mockReview.getUser()).willReturn(mockReviewOwner);
        given(mockReview.getContent()).willReturn("리뷰 내용입니다");

        given(reviewRepository.findById(testReviewId)).willReturn(Optional.of(mockReview));

        // 2. DTO 대신 PopularReview 엔티티 생성
        PopularReview popularReview = new PopularReview(
                PeriodType.DAILY,
                1L,
                100.0,
                5L,
                2L,
                mockReview
        );

        Alarm mockAlarm = mock(Alarm.class);
        given(alarmRepository.save(any(Alarm.class))).willReturn(mockAlarm);
        given(alarmMapper.alarmToNotificationDto(mockAlarm)).willReturn(mock(NotificationDto.class));

        // when
        // DTO가 아닌 popularReview 엔티티를 전달합니다.
        NotificationDto result = alarmService.createReviewAlarm(popularReview);

        // then
        assertThat(result).isNotNull();
        verify(alarmRepository).save(any(Alarm.class));
    }
}