package com.redhorse.deokhugam.domain.dashboard.entity;

import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.global.entity.BaseEntity;
import com.redhorse.deokhugam.global.entity.PeriodType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "power_users")
@Getter
public class PowerUser extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "period", length = 20, nullable = false)
    private PeriodType period;

    @Column(name = "ranking", nullable = false)
    private Long ranking;

    @Column(name = "score", nullable = false)
    private Double score = 0.0;

    @Column(name = "review_score_sum", nullable = false)
    private Double reviewScoreSum = 0.0;

    @Column(name = "like_count", nullable = false)
    private Long likeCount = 0L;

    @Column(name = "comment_count", nullable = false)
    private Long commentCount = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_power_users_users"))
    private User user;

    public PowerUser(PeriodType period, Long ranking, Double score, Double reviewScoreSum, Long likeCount, Long commentCount, User user) {
        this.period = period;
        this.ranking = ranking;
        this.score = score;
        this.reviewScoreSum = reviewScoreSum;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.user = user;
    }
}
