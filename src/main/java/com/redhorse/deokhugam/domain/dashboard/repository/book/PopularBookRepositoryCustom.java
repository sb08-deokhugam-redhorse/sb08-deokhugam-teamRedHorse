package com.redhorse.deokhugam.domain.dashboard.repository.book;
import com.redhorse.deokhugam.domain.dashboard.dto.request.DashboardRequest;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularBook;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PopularBookRepositoryCustom {
    Slice<PopularBook> getAllPopularBook(DashboardRequest request, Pageable pageable);
}