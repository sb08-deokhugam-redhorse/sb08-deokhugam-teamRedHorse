package com.redhorse.deokhugam.domain.user.entity;

import com.redhorse.deokhugam.domain.alarm.entity.Alarm;
import com.redhorse.deokhugam.domain.comment.entity.Comment;
import com.redhorse.deokhugam.domain.dashboard.entity.PowerUser;
import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.domain.review.entity.ReviewLike;
import com.redhorse.deokhugam.global.entity.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@SQLRestriction("is_deleted = false")
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity {
    @Column(columnDefinition = "timestamp with time zone")
    private Instant deletedAt;

    @Column(name="email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "nickname", nullable = false, length = 20)
    private String nickname;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    /* ========================================================
       연관관계 매핑
       [미션 요구 사항] 물리 삭제 시 관련된 정보도 모두 삭제되도록 하세요.
       ======================================================== */
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReviewLike> reviewLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Alarm> alarms = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PowerUser> powerUsers = new ArrayList<>();

    public User(String email, String nickname, String password) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }

    /**
     * <p>[미션 요구사항] 닉네임만 수정할 수 있습니다.</p>
     *
     * @param nickname 수정할 닉네임
     */
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 논리 삭제 처리
     */
    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = Instant.now();
    }
}
