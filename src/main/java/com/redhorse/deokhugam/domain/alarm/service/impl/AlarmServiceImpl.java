package com.redhorse.deokhugam.domain.alarm.service.impl;

import com.redhorse.deokhugam.domain.alarm.dto.NotificationDto;
import com.redhorse.deokhugam.domain.alarm.entity.Alarm;
import com.redhorse.deokhugam.domain.alarm.mapper.AlarmMapper;
import com.redhorse.deokhugam.domain.alarm.repository.AlarmRepository;
import com.redhorse.deokhugam.domain.alarm.service.AlarmService;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto; // 이거 임시임
import com.redhorse.deokhugam.domain.comment.repository.CommentRepository;
import com.redhorse.deokhugam.domain.review.repository.ReviewRepository;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

        Alarm alarm = new Alarm(
                "COMMENT",
                dto.content(),
                dto.userNickName(),
                "여기에 링크 넣기",
                user.get()
        );

         alarm = alarmRepository.save(alarm);

         NotificationDto notificationDto = new NotificationDto(
                 alarm.getId(),
                 alarm.getSender(),
                 alarm.getRecipient().getId(),
         );

        return



                .contents(dto.content()) // 댓글 내용
                .sender(dto.userNickName()) // 발송자 (댓글 작성자 닉네임)
                .link("/reviews/" + dto.reviewId()) // 이동할 링크 조립
                .recipient(recipient) // 수신자 엔티티
                .build();

        alarmRepository.save(alarmMapper.createNotificationDtoToCommentDto(dto));
        return  null;
    }

    @Override
    public NotificationDto createDashboardAlarm(Object dto) {

        return  null;
    }

//    @Override
//    public void checkAlarm() {
//
//    }
//
//    @Override
//    public void checkAllAlarm() {
//
//    }
//
//    @Override
//    public void deleteAlarm() {
//
//    }
//
//    @Override
//    public void getAlarmList() {
//
//    }
}
