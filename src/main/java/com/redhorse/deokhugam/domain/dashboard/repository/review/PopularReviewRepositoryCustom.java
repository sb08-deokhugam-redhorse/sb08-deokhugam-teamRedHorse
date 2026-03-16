package com.redhorse.deokhugam.domain.dashboard.repository.review;
import com.redhorse.deokhugam.domain.dashboard.dto.request.DashboardRequest;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularBook;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularReview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PopularReviewRepositoryCustom {
    Slice<PopularReview> getAllPopularReview(DashboardRequest request, Pageable pageable);
}