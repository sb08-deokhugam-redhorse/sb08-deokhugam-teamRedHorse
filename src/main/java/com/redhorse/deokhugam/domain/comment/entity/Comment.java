package com.redhorse.deokhugam.domain.comment.entity;

import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.global.entity.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseUpdatableEntity {
    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_comments_review"))
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_comments_user"))
    private User user;

    public Comment(String content, Review review, User user) {
        this.content = content;
        this.review = review;
        this.user = user;
    }

    public void update(String newContent) {
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
        }
    }

    public void softDelete() {
        this.deletedAt = Instant.now();
    }
}
