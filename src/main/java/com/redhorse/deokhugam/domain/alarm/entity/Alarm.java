package com.redhorse.deokhugam.domain.alarm.entity;

import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.global.entity.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "alarms")
@Getter
public class Alarm extends BaseUpdatableEntity {
    @Column(name = "type", length = 10, nullable = false)
    private String type;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "review_content", length = 255, nullable = false)
    private String reviewContent;

    @Column(name = "review_id", nullable = false)
    private UUID reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_alarms_users"))
    private User user;

    public Alarm(String type, String message, String reviewContent, UUID reviewId, User user) {
        this.type = type;
        this.message = message;
        this.reviewContent = reviewContent;
        this.reviewId = reviewId;
        this.user = user;
    }
}
