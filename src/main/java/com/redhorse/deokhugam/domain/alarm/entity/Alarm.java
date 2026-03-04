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

    @Column(name = "contents", length = 255, nullable = false)
    private String contents;

    @Column(name = "sender", length = 100, nullable = false)
    private UUID sender;

    @Column(name = "link", length = 255, nullable = false)
    private String link;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_alarms_users"))
    private User recipient;

    public Alarm(String type, String contents, String sender, String link, User recipient) {
        this.type = type;
        this.contents = contents;
        this.sender = sender;
        this.link = link;
        this.recipient = recipient;
    }
}
