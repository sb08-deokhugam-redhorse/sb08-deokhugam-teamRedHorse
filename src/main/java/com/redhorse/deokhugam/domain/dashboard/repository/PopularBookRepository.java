package com.redhorse.deokhugam.domain.dashboard.repository;

import com.redhorse.deokhugam.domain.dashboard.dto.request.DashboardRequest;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularBook;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PopularBookRepository extends JpaRepository<PopularBook, UUID> {

    @Query("SELECT a FROM PopularBook a " +
            "WHERE a.period = :#{#request.period} " +
            "AND (" +
            "  (:#{#request.after == null ? true : false} = true) OR " +
            "  (a.createdAt < :#{#request.after}) OR " +
            "  (a.createdAt = :#{#request.after} AND a.id < :#{#request.cursor}) " +
            ")"+
            "ORDER BY a.ranking DESC")
    Slice<PopularBook> getAllPopularBookDesc(@Param("request") DashboardRequest request, Pageable pageable);

    @Query("SELECT a FROM PopularBook a " +
            "WHERE a.period = :#{#request.period} " +
            "AND (" +
            "  (:#{#request.after == null ? true : false} = true) OR " +
            "  (a.createdAt > :#{#request.after}) " +
            "  OR (a.createdAt = :#{#request.after} AND a.id > :#{#request.cursor})" +
            ")"+
            "ORDER BY a.ranking ASC")
    Slice<PopularBook> getAllPopularBookAsc(@Param("request") DashboardRequest request, Pageable pageable);
}
