package com.redhorse.deokhugam.domain.review.repository;

import com.redhorse.deokhugam.domain.review.entity.Review;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, UUID>, ReviewRepositoryCustom {

  // 쓰기용
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select r from Review r where r.id= :id AND r.deletedAt is null")
  Optional<Review> findByIdForUpdate(@Param("id") UUID id);

  // 조회용
  @Cacheable(value = "review", key = "#reviewId")
  Optional<Review> findByIdAndDeletedAtIsNull(UUID reviewId);

  boolean existsByIdAndDeletedAtIsNull(UUID id);

  Boolean existsByBookIdAndUserId(UUID bookId, UUID userId);
}
