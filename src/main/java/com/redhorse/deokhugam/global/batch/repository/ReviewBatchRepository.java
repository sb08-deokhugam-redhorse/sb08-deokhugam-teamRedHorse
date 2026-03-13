package com.redhorse.deokhugam.global.batch.repository;

import com.redhorse.deokhugam.domain.dashboard.dto.popularreview.ReviewBatchDto;
import com.redhorse.deokhugam.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface ReviewBatchRepository extends JpaRepository<Review, UUID> {
}