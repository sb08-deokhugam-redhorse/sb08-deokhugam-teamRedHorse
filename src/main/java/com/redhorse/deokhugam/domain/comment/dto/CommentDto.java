package com.redhorse.deokhugam.domain.comment.dto;

import java.util.UUID;

public record CommentDto(
  UUID id,
  UUID reviewId,
  UUID userId,
  String userNickname,
  String content,
  String createdAt,
  String updatedAt) {

}
