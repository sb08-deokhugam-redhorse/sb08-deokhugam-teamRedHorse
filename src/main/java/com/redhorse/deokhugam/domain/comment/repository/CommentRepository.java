package com.redhorse.deokhugam.domain.comment.repository;

import com.redhorse.deokhugam.domain.comment.entity.Comment;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

  Optional<Comment> findByIdAndDeletedAtIsNull(UUID commentId);

  @Query("SELECT c FROM Comment c WHERE c.review.id = :reviewId " +
      "AND c.deletedAt IS NULL " +
      "AND (:cursor IS NULL OR c.createdAt > :after OR (c.createdAt = :after AND c.id > :cursor)) "
      +
      "ORDER BY c.createdAt ASC, c.id ASC")
  List<Comment> findAllByCursorAsc(
      @Param("reviewId") UUID reviewId,
      @Param("cursor") UUID cursor,
      @Param("after") Instant after,
      Pageable pageable);

  @Query("SELECT c FROM Comment c WHERE c.review.id = :reviewId " +
      "AND c.deletedAt IS NULL " +
      "AND (:cursor IS NULL OR c.createdAt < :after OR (c.createdAt = :after AND c.id < :cursor)) "
      +
      "ORDER BY c.createdAt DESC, c.id DESC")
  List<Comment> findAllByCursorDesc(
      @Param("reviewId") UUID reviewId,
      @Param("cursor") UUID cursor,
      @Param("after") Instant after,
      Pageable pageable);

  long countByReviewIdAndDeletedAtIsNull(UUID reviewId);
}
