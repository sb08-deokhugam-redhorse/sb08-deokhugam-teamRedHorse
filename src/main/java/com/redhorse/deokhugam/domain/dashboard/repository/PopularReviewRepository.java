package com.redhorse.deokhugam.domain.dashboard.repository;

import com.redhorse.deokhugam.domain.dashboard.dto.request.DashboardRequest;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularReview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.UUID;

public interface PopularReviewRepository extends JpaRepository<PopularReview, UUID> {

    @Query("SELECT a FROM PopularReview a " +
            "WHERE a.period = :#{#request.period} " +
            "AND FUNCTION('DATE', a.createdAt) = :yesterday ")
    Slice<PopularReview> getAllPopularReview(
            @Param("request") DashboardRequest request,
            @Param("yesterday") LocalDate yesterday,
            Pageable pageable);


    @Query("SELECT COUNT(a) FROM PopularReview a " +
            "WHERE a.period = :#{#request.period} " +
            "AND FUNCTION('DATE', a.createdAt) = :yesterday ")
    Long countByRequest(@Param("request") DashboardRequest request, @Param("yesterday") LocalDate yesterday);
}
