package com.redhorse.deokhugam.domain.dashboard.repository.user;
import com.redhorse.deokhugam.domain.dashboard.dto.request.DashboardRequest;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularReview;
import com.redhorse.deokhugam.domain.dashboard.entity.PowerUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PowerUserRepositoryCustom {
    Slice<PowerUser> getAllPowerUser(DashboardRequest request, Pageable pageable);
}