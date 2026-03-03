package com.redhorse.deokhugam.domain.alarm.mapper;

import com.redhorse.deokhugam.domain.alarm.dto.NotificationDto;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AlarmMapper {
    default NotificationDto createNotificationDtoToCommentDto(CommentDto dto){

        return NotificationDto.builder()
    }
}
