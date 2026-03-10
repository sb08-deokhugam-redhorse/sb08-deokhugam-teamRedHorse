package com.redhorse.deokhugam.global.batch.repository;

import com.redhorse.deokhugam.domain.dashboard.dto.popularreview.ReivewBatchDto;
import com.redhorse.deokhugam.domain.dashboard.dto.poweruser.UserBatchDto;
import com.redhorse.deokhugam.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface ReviewBatchRepository extends JpaRepository<Review, UUID> {

    @Query("SELECT new com.redhorse.deokhugam.domain.dashboard.dto.popularreview.ReivewBatchDto(" +
            "CAST(:period AS string), r.id, r.commentCount,  r.likeCount, CAST(r.likeCount * 0.3 + r.commentCount * 0.7 AS double)) " +
            "FROM Review r " +
            "WHERE r.createdAt >= :startDay " +
            "AND r.createdAt < :endDay " +
            "AND r.deletedAt IS NULL " +
            "ORDER BY r.likeCount * 0.3 + r.commentCount * 0.7 DESC")
    Page<ReivewBatchDto> findReviews(
            @Param("period") String period,
            @Param("startDay") Instant startDay,
            @Param("endDay") Instant endDay,
            Pageable pageable
    );


}
