package com.redhorse.deokhugam.domain.dashboard.repository;

import com.redhorse.deokhugam.domain.dashboard.dto.request.DashboardRequest;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularBook;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.UUID;

public interface PopularBookRepository extends JpaRepository<PopularBook, UUID> {
    @Query("SELECT a FROM PopularBook a " +
            "WHERE a.period = :#{#request.period} " +
            "AND FUNCTION('DATE', a.createdAt) = :yesterday ")
    Slice<PopularBook> getAllPopularBook(
            @Param("request") DashboardRequest request,
            @Param("yesterday") LocalDate yesterday,
            Pageable pageable);

    @Query("SELECT COUNT(a) FROM PopularReview a " +
            "WHERE a.period = :#{#request.period} " +
            "AND FUNCTION('DATE', a.createdAt) = :yesterday ")
    Long countByRequestAndDate(@Param("request") DashboardRequest request, @Param("yesterday") LocalDate yesterday);
}
