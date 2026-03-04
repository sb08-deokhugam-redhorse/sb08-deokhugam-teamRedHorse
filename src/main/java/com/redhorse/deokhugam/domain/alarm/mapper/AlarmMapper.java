package com.redhorse.deokhugam.domain.alarm.mapper;

import com.redhorse.deokhugam.domain.alarm.dto.NotificationDto;
import com.redhorse.deokhugam.domain.alarm.entity.Alarm;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import com.redhorse.deokhugam.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AlarmMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "confirmed", constant = "false")
    NotificationDto alarmToNotificationDto(Alarm alarm);

}
