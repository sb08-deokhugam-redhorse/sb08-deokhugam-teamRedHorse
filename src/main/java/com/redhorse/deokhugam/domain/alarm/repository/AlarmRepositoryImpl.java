package com.redhorse.deokhugam.domain.alarm.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationListRequest;
import com.redhorse.deokhugam.domain.alarm.entity.Alarm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.UUID;

import static com.redhorse.deokhugam.domain.alarm.entity.QAlarm.alarm;

@RequiredArgsConstructor
public class AlarmRepositoryImpl implements AlarmRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Alarm> getAllAlarms(NotificationListRequest request, Pageable pageable) {
        List<Alarm> content = queryFactory
                .selectFrom(alarm)
                .where(
                        alarm.user.id.eq(request.userId()),
                        cursorCondition(request)
                )
                .orderBy(createOrderSpecifiers(request))
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return checkLastPage(pageable, content);
    }

    private OrderSpecifier<?>[] createOrderSpecifiers(NotificationListRequest request) {
        if ("ASC".equalsIgnoreCase(request.direction())) {
            return new OrderSpecifier<?>[]{
                    alarm.createdAt.asc(),
                    alarm.id.asc()
            };
        }

        return new OrderSpecifier<?>[]{
                alarm.createdAt.desc(),
                alarm.id.desc()
        };
    }

    private BooleanExpression cursorCondition(NotificationListRequest request) {
        if (request.after() == null) return null;

        if (request.direction().equals("DESC")) {
            return alarm.createdAt.lt(request.after())
                    .or(alarm.createdAt.eq(request.after())
                            .and(alarm.id.lt(UUID.fromString(request.cursor()))));
        }

        return alarm.createdAt.gt(request.after())
                .or(alarm.createdAt.eq(request.after())
                        .and(alarm.id.gt(UUID.fromString(request.cursor()))));
    }

    private Slice<Alarm> checkLastPage(Pageable pageable, List<Alarm> content) {
        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }
}