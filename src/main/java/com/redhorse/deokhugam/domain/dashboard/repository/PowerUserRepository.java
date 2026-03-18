package com.redhorse.deokhugam.domain.dashboard.repository;

import com.redhorse.deokhugam.domain.dashboard.dto.request.DashboardRequest;
import com.redhorse.deokhugam.domain.dashboard.entity.PowerUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PowerUserRepository extends JpaRepository<PowerUser, UUID> {

    @Query("SELECT a FROM PowerUser a " +
            "WHERE a.period = :#{#request.period} " +
            "AND FUNCTION('DATE', a.createdAt) = CURRENT_DATE " +
            "ORDER BY a.ranking DESC")
    Slice<PowerUser> getAllPowerUser(@Param("request") DashboardRequest request, Pageable pageable);
}
