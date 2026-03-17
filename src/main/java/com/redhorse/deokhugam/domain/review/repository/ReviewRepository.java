package com.redhorse.deokhugam.domain.review.repository;

import com.redhorse.deokhugam.domain.review.entity.Review;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, UUID>, ReviewRepositoryCustom {

  // 쓰기용
  @EntityGraph(attributePaths = {"book", "user"})
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select r from Review r where r.id= :id AND r.deletedAt is null")
  Optional<Review> findByIdForUpdate(@Param("id") UUID id);

  // 조회용
  @EntityGraph(attributePaths = {"book", "user"})
  @Cacheable(value = "review", key = "#reviewId")
  Optional<Review> findByIdAndDeletedAtIsNull(UUID reviewId);

  boolean existsByIdAndDeletedAtIsNull(UUID id);

  boolean existsByBookIdAndUserIdAndDeletedAtIsNull(UUID bookId, UUID userId);

  @Query("SELECT COUNT(r) FROM Review r WHERE r.book.id = :bookId AND r.deletedAt IS NULL")
  long countByBookId(UUID bookId);

  @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId AND r.deletedAt IS NULL")
  Double averageRatingByBookId(@Param("bookId") UUID bookId);
}
