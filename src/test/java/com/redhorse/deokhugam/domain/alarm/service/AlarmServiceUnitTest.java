package com.redhorse.deokhugam.domain.alarm.service;

import com.redhorse.deokhugam.domain.alarm.dto.NotificationDto;
import com.redhorse.deokhugam.domain.alarm.entity.Alarm;
import com.redhorse.deokhugam.domain.alarm.mapper.AlarmMapper;
import com.redhorse.deokhugam.domain.alarm.repository.AlarmRepository;
import com.redhorse.deokhugam.domain.alarm.service.impl.AlarmServiceImpl;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import com.redhorse.deokhugam.domain.dashboard.dto.poweruser.PowerUserDto;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularReview;
import com.redhorse.deokhugam.domain.dashboard.entity.PowerUser;
import com.redhorse.deokhugam.domain.review.dto.ReviewLikeDto;
import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.domain.review.exception.ReviewNotFoundException;
import com.redhorse.deokhugam.domain.review.repository.ReviewRepository;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.exception.UserNotFoundException;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import com.redhorse.deokhugam.global.entity.PeriodType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
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
@DisplayName("알림 생성 테스트")
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
    @DisplayName("댓글 알림 생성 성공")
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
        alarmService.createCommentAlarm(dto);

        // then
        ArgumentCaptor<Alarm> alarmCaptor = ArgumentCaptor.forClass(Alarm.class);
        verify(alarmRepository).save(alarmCaptor.capture());

        Alarm savedAlarm = alarmCaptor.getValue();
        assertThat(savedAlarm.getType()).isEqualTo("COMMENT");
        assertThat(savedAlarm.getMessage()).contains("님이 나의 리뷰에 댓글을 남겼습니다.");
    }

    @Test
    @DisplayName("댓글 알림 생성 실패 - 리뷰가 존재하지 않음")
    void createCommentAlarm_Fail_ReviewNotFound() {
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
                .isInstanceOf(ReviewNotFoundException.class);
    }

    @Test
    @DisplayName("댓글 알림 생성 실패 - 댓글 작성자(유저)가 존재하지 않음")
    void createCommentAlarm_Fail_UserNotFound() {
        // given
        CommentDto dto = new CommentDto(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "닉네임", "내용", Instant.now(), Instant.now());
        Review mockReview = mock(Review.class);

        given(reviewRepository.findById(dto.reviewId())).willReturn(Optional.of(mockReview));
        given(mockReview.getUser()).willReturn(mock(User.class));
        given(userRepository.findById(dto.userId())).willReturn(Optional.empty()); // 유저 없음 설정

        // when & then
        assertThatThrownBy(() -> alarmService.createCommentAlarm(dto))
                .isInstanceOf(UserNotFoundException.class);
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
        alarmService.createLikeAlarm(dto);

        // then
        verify(alarmRepository).save(any(Alarm.class));
    }

    @Test
    @DisplayName("좋아요 알림 생성 실패 - 유저가 없음")
    void createLikeAlarm_Fail_UserNotFound() {
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
    @DisplayName("좋아요 알림 생성 실패 - 리뷰가 없음")
    void createLikeAlarm_Fail_ReviewNotFound() {
        // given
        ReviewLikeDto dto = new ReviewLikeDto(UUID.randomUUID(), UUID.randomUUID(), true);
        User mockLiker = mock(User.class);

        given(userRepository.findById(dto.userId())).willReturn(Optional.of(mockLiker));
        given(reviewRepository.findById(dto.reviewId())).willReturn(Optional.empty()); // 리뷰 없음 설정

        // when & then
        assertThatThrownBy(() -> alarmService.createLikeAlarm(dto))
                .isInstanceOf(ReviewNotFoundException.class);
    }

    @ParameterizedTest
    @EnumSource(PeriodType.class)
    @DisplayName("인기 리뷰 알림 생성 성공")
    void createReviewAlarm_Success(PeriodType periodType) {
        // given
        UUID testReviewId = UUID.randomUUID();
        Review mockReview = mock(Review.class);
        User mockReviewOwner = mock(User.class);

        given(mockReview.getId()).willReturn(testReviewId);
        given(mockReview.getUser()).willReturn(mockReviewOwner);
        given(mockReview.getContent()).willReturn("리뷰 내용입니다");
        given(reviewRepository.findById(testReviewId)).willReturn(Optional.of(mockReview));

        PopularReview popularReview = new PopularReview(periodType, 1L, 100.0, 5L, 2L, mockReview);

        Alarm mockAlarm = mock(Alarm.class);
        given(alarmRepository.save(any(Alarm.class))).willReturn(mockAlarm);
        given(alarmMapper.alarmToNotificationDto(mockAlarm)).willReturn(mock(NotificationDto.class));

        // when
        alarmService.createReviewAlarm(popularReview);

        ArgumentCaptor<Alarm> alarmCaptor = ArgumentCaptor.forClass(Alarm.class);
        verify(alarmRepository).save(alarmCaptor.capture());

        // then
        assertThat(alarmCaptor.getValue().getType()).isEqualTo(periodType.toString());
    }

    @Test
    @DisplayName("인기 리뷰 알림 생성 실패 - 해당 리뷰가 존재하지 않음")
    void createReviewAlarm_Fail_ReviewNotFound() {
        // given
        Review mockReview = mock(Review.class);
        given(mockReview.getId()).willReturn(UUID.randomUUID());
        PopularReview popularReview = new PopularReview(PeriodType.DAILY, 1L, 100.0, 5L, 2L, mockReview);

        given(reviewRepository.findById(mockReview.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> alarmService.createReviewAlarm(popularReview))
                .isInstanceOf(ReviewNotFoundException.class);
    }

    @ParameterizedTest
    @EnumSource(PeriodType.class)
    @DisplayName("파워 유저 알림 생성 성공 - 기간별 테스트")
    void createPowerUserAlarm_Success(PeriodType periodType) {
        // given
        UUID testUserId = UUID.randomUUID();
        PowerUserDto dto = mock(PowerUserDto.class);
        given(dto.userId()).willReturn(testUserId);
        given(dto.period()).willReturn(periodType);
        given(dto.rank()).willReturn(1L);

        User mockUser = mock(User.class);
        given(mockUser.getNickname()).willReturn("테스트유저");
        given(userRepository.findById(testUserId)).willReturn(Optional.of(mockUser));

        Alarm mockAlarm = mock(Alarm.class);
        given(alarmRepository.save(any(Alarm.class))).willReturn(mockAlarm);
        given(alarmMapper.alarmToNotificationDto(mockAlarm)).willReturn(mock(NotificationDto.class));

        // when
        alarmService.createPowerUserAlarm(dto);

        // then
        ArgumentCaptor<Alarm> alarmCaptor = ArgumentCaptor.forClass(Alarm.class);
        verify(alarmRepository).save(alarmCaptor.capture());

        Alarm savedAlarm = alarmCaptor.getValue();

        assertThat(savedAlarm.getType()).isEqualTo(periodType.toString());
        assertThat(savedAlarm.getMessage()).contains("1위");
    }

    @Test
    @DisplayName("파워 유저 알림 생성 실패 - 선정된 유저를 찾을 수 않음")
    void createPowerUserAlarm_Fail_UserNotFound() {
        // given
        PowerUserDto dto = mock(PowerUserDto.class);
        given(dto.userId()).willReturn(UUID.randomUUID());

        given(userRepository.findById(dto.userId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> alarmService.createPowerUserAlarm(dto))
                .isInstanceOf(UserNotFoundException.class);
    }
}