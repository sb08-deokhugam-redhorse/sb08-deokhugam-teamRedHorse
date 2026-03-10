package com.redhorse.deokhugam.domain.alarm.mapper;

import com.redhorse.deokhugam.domain.alarm.dto.NotificationDto;
import com.redhorse.deokhugam.domain.alarm.entity.Alarm;
import com.redhorse.deokhugam.domain.dashboard.dto.popularreview.PopularReviewDto;
import com.redhorse.deokhugam.domain.dashboard.dto.poweruser.PowerUserDto;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularReview;
import com.redhorse.deokhugam.domain.dashboard.entity.PowerUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AlarmMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "confirmed", constant = "false") // 추후 수정, 생성시면 상관 없는데 업뎃에서 걸릴 듯
    NotificationDto alarmToNotificationDto(Alarm alarm);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.nickname", target = "nickname")
    @Mapping(source = "ranking", target = "rank")
    PowerUserDto toPowerUserDto(PowerUser powerUser);
}
