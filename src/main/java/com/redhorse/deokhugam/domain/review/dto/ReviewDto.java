package com.redhorse.deokhugam.domain.review.dto;

import java.time.Instant;
import java.util.UUID;

public record ReviewDto(

    UUID id,
    UUID bookId,
    String bookTitle,
    String bookThumbnailUrl,
    UUID userId,
    String userNickname,
    String content,
    Integer rating,
    long likeCount,
    long commentCount,
    boolean likedByMe,
    Instant createdAt,
    Instant updatedAt
) {

}
