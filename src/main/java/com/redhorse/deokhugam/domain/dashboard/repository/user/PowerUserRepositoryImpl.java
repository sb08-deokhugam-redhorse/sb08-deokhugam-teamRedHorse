package com.redhorse.deokhugam.domain.dashboard.repository.user;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.redhorse.deokhugam.domain.dashboard.dto.request.DashboardRequest;
import com.redhorse.deokhugam.domain.dashboard.entity.PowerUser;
import com.redhorse.deokhugam.global.entity.PeriodType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.redhorse.deokhugam.domain.dashboard.entity.QPowerUser.powerUser;

@RequiredArgsConstructor
public class PowerUserRepositoryImpl implements PowerUserRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<PowerUser> getAllPowerUser(DashboardRequest request, Pageable pageable) {
        List<PowerUser> content = queryFactory
                .selectFrom(powerUser)
                .where(
                        periodEq(request.period()),
                        cursorCondition(request)

                ).orderBy(
                        request.direction().equals("DESC")
                                ? powerUser.ranking.desc()
                                : powerUser.ranking.asc()

                ).limit(pageable.getPageSize() + 1)
                .fetch();

        return checkLastPage(pageable, content);
    }

    private BooleanExpression periodEq(PeriodType period) {
        return period != null ? powerUser.period.eq(period) : null;
    }

    private BooleanExpression cursorCondition(DashboardRequest request) {
        if (request.after() == null) return null;

        if (request.direction().equals("DESC")) {
            return powerUser.createdAt.lt(request.after())
                    .or(powerUser.createdAt.eq(request.after()).and(powerUser.id.lt(request.cursor())));
        }

        return powerUser.createdAt.gt(request.after())
                .or(powerUser.createdAt.eq(request.after()).and(powerUser.id.gt(request.cursor())));
    }

    private Slice<PowerUser> checkLastPage(Pageable pageable, List<PowerUser> content) {
        boolean hasNext = false;

        if (content.size() > pageable.getPageSize()) {
            hasNext = true;
            content.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }
}