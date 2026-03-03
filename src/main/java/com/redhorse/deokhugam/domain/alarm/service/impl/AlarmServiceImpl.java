package com.redhorse.deokhugam.domain.alarm.service.impl;

import com.redhorse.deokhugam.domain.alarm.dto.NotificationDto;
import com.redhorse.deokhugam.domain.alarm.mapper.AlarmMapper;
import com.redhorse.deokhugam.domain.alarm.repository.AlarmRepository;
import com.redhorse.deokhugam.domain.alarm.service.AlarmService;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto; // 이거 임시임
import com.redhorse.deokhugam.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {
    private final AlarmRepository alarmRepository;
    private final ReviewRepository reviewRepository;
    private final AlarmMapper alarmMapper;

    @Override
    public NotificationDto createCommentAlarm(CommentDto dto) {
        // 임시 dto 사용 중
//        dto.id =
        return  null;
    }

    @Override
    public NotificationDto createDashboardAlarm(Object dto) {

        return  null;
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
