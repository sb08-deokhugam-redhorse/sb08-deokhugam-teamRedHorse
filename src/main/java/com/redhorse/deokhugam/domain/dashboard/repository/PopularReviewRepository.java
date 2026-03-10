package com.redhorse.deokhugam.domain.dashboard.repository;

import com.redhorse.deokhugam.domain.dashboard.entity.PopularReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PopularReviewRepository extends JpaRepository<PopularReview, UUID> {
}
