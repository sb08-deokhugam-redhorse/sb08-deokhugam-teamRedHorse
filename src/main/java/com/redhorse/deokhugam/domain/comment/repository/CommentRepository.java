package com.redhorse.deokhugam.domain.comment.repository;

import com.redhorse.deokhugam.domain.comment.entity.Comment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, UUID>, CommentRepositoryCustom {

  Optional<Comment> findByIdAndDeletedAtIsNull(UUID commentId);

  long countByReviewIdAndDeletedAtIsNull(UUID reviewId);

  @Query("select c.review.id , count(c.id)from Comment c where c.review.id in :reviewIds and c.deletedAt is null group by c.review.id")
  List<Object[]> commentCount(List<UUID> reviewIds);
}
