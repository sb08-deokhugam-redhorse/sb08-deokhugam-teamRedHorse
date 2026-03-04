package com.redhorse.deokhugam.domain.comment.repository;

import com.redhorse.deokhugam.domain.comment.entity.Comment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

  Optional<Comment> findByIdAndDeletedAtIsNull(UUID id);
}
