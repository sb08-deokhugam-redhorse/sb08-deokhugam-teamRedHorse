package com.redhorse.deokhugam.domain.dashboard.entity;

import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.global.entity.BaseEntity;
import com.redhorse.deokhugam.global.entity.PeriodType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "popular_reviews")
@Getter
public class PopularReview extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "period", length = 20, nullable = false)
    private PeriodType period;

    @Column(name = "ranking", nullable = false)
    private Long ranking;

    @Column(name = "score", nullable = false)
    private Double score = 0.0;

    @Column(name = "like_count", nullable = false)
    private Long likeCount = 0L;

    @Column(name = "comment_count", nullable = false)
    private Long commentCount = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_popular_review_review"))
    private Review review;

    public PopularReview(PeriodType period, Long ranking, Double score, Long likeCount, Long commentCount, Review review) {
        this.period = period;
        this.ranking = ranking;
        this.score = score;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.review = review;
    }
}
