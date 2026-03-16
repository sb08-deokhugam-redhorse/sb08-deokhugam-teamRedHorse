package com.redhorse.deokhugam.domain.dashboard.repository.book;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.redhorse.deokhugam.domain.dashboard.dto.request.DashboardRequest;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularBook;
import com.redhorse.deokhugam.global.entity.PeriodType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.redhorse.deokhugam.domain.dashboard.entity.QPopularBook.popularBook;

@RequiredArgsConstructor
public class PopularBookRepositoryImpl implements PopularBookRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<PopularBook> getAllPopularBook(DashboardRequest request, Pageable pageable) {
        List<PopularBook> content = queryFactory
                .selectFrom(popularBook)
                .where(
                        periodEq(request.period()),
                        cursorCondition(request)

                ).orderBy(
                        request.direction().equals("DESC")
                                ? popularBook.ranking.desc()
                                : popularBook.ranking.asc()

                ).limit(pageable.getPageSize() + 1)
                .fetch();

        return checkLastPage(pageable, content);
    }

    private BooleanExpression periodEq(PeriodType period) {
        return period != null ? popularBook.period.eq(period) : null;
    }

    private BooleanExpression cursorCondition(DashboardRequest request) {
        if (request.after() == null) return null;

        if (request.direction().equals("DESC")) {
            return popularBook.createdAt.lt(request.after())
                    .or(popularBook.createdAt.eq(request.after()).and(popularBook.id.lt(request.cursor())));
        }

        return popularBook.createdAt.gt(request.after())
                .or(popularBook.createdAt.eq(request.after()).and(popularBook.id.gt(request.cursor())));
    }

    private Slice<PopularBook> checkLastPage(Pageable pageable, List<PopularBook> content) {
        boolean hasNext = false;

        if (content.size() > pageable.getPageSize()) {
            hasNext = true;
            content.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }
}