package com.redhorse.deokhugam.domain.review.repository;

import com.redhorse.deokhugam.domain.review.entity.ReviewLike;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, UUID> {

  // 연속으로 좋아요를 누를 경우 오류를 막기 위해서
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select rl from ReviewLike rl where rl.review.id= :reviewId AND rl.user.id= :userId")
  Optional<ReviewLike> findByIdForUpdate(
      @Param("reviewId") UUID reviewId,
      @Param("userId") UUID userId);

  // 조회용
  Optional<ReviewLike> findByReviewIdAndUserIdAndDeletedAtIsNull(UUID reviewId, UUID userId);

  // 리뷰 목록 조회 때 리뷰 좋아요 추가
  List<ReviewLike> findAllByUserIdAndReviewIdInAndDeletedAtIsNull(UUID userId,
      List<UUID> reviewIds);
}
