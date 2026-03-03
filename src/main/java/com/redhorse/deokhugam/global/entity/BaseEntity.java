package com.redhorse.deokhugam.global.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity
{
    @GeneratedValue(strategy = GenerationType.UUID) // hibernate 6.2+
    @Column(name = "id", columnDefinition = "UUID", updatable = false)
    @Id
    private UUID id;

    @Column(name = "created_at", updatable = false, nullable = false)
    @CreatedDate
    private Instant createdAt;
}

