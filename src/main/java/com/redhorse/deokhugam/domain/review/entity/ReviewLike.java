package com.redhorse.deokhugam.domain.review.entity;

import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.global.entity.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@Table(name = "review_likes", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_user_review_like_id",
                columnNames = {"user_id", "review_id"}
        )
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewLike extends BaseUpdatableEntity {

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_reviews_likes_user_id"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_review_id"))
    private Review review;

    public ReviewLike(User user, Review review) {
        this.user = user;
        this.review = review;
    }
}
