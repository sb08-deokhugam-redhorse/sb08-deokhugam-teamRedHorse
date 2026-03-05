package com.redhorse.deokhugam.domain.review.repository;

import com.redhorse.deokhugam.domain.review.entity.Review;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

  Optional<Review> findByIdAndDeletedAtIsNull(UUID reviewId);
}
