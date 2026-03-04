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
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "message", source = "content")
//    @Mapping(target = "confirmed", constant = "false")
//    @Mapping(target = "reviewContent", constant = "COMMENT")
//    NotificationDto createNotificationDtoToCommentDto(Alarm alarm);
}
