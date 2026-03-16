package com.redhorse.deokhugam.domain.dashboard.repository.review;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.redhorse.deokhugam.domain.dashboard.dto.request.DashboardRequest;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularReview;
import com.redhorse.deokhugam.global.entity.PeriodType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.redhorse.deokhugam.domain.dashboard.entity.QPopularReview.popularReview;

@RequiredArgsConstructor
public class PopularReviewRepositoryImpl implements PopularReviewRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<PopularReview> getAllPopularReview(DashboardRequest request, Pageable pageable) {
        List<PopularReview> content = queryFactory
                .selectFrom(popularReview)
                .where(
                        periodEq(request.period()),
                        cursorCondition(request)

                ).orderBy(
                        request.direction().equals("DESC")
                                ? popularReview.ranking.desc()
                                : popularReview.ranking.asc()

                ).limit(pageable.getPageSize() + 1)
                .fetch();

        return checkLastPage(pageable, content);
    }

    private BooleanExpression periodEq(PeriodType period) {
        return period != null ? popularReview.period.eq(period) : null;
    }

    private BooleanExpression cursorCondition(DashboardRequest request) {
        if (request.after() == null) return null;

        if (request.direction().equals("DESC")) {
            return popularReview.createdAt.lt(request.after())
                    .or(popularReview.createdAt.eq(request.after()).and(popularReview.id.lt(request.cursor())));
        }

        return popularReview.createdAt.gt(request.after())
                .or(popularReview.createdAt.eq(request.after()).and(popularReview.id.gt(request.cursor())));
    }

    private Slice<PopularReview> checkLastPage(Pageable pageable, List<PopularReview> content) {
        boolean hasNext = false;

        if (content.size() > pageable.getPageSize()) {
            hasNext = true;
            content.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }
}