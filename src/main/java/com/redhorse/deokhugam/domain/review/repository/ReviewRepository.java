package com.redhorse.deokhugam.domain.review.repository;

import com.redhorse.deokhugam.domain.review.entity.Review;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, UUID>,ReviewRepositoryCustom {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select r from Review r where r.id= :id AND r.deletedAt is null")
  Optional<Review> findByIdAndDeletedAtIsNull(@Param("id") UUID id);

}
