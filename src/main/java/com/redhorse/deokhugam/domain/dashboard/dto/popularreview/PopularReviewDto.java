package com.redhorse.deokhugam.domain.dashboard.dto.popularreview;

import com.redhorse.deokhugam.global.entity.PeriodType;

import java.time.Instant;
import java.util.UUID;

public record PopularReviewDto(
        UUID id,
        UUID reviewId,
        UUID bookId,
        String bookTitle,
        String bookThumbnailUrl,
        UUID userId,
        String userNickname,
        int reviewCount,
        double rivewRating,
        PeriodType period,
        Instant createAt,
        int rank,
        double score,
        int likeCount,
        int commentCount
) {
}
