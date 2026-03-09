package com.redhorse.deokhugam.domain.book.entity;

import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.global.entity.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SQLRestriction("is_deleted = false")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "books")
@Entity
public class Book extends BaseUpdatableEntity
{
    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "author", length = 50, nullable = false)
    private String author;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "publisher", length = 50, nullable = false)
    private String publisher;

    @Column(name = "published_date", nullable = false)
    private LocalDate publishedDate;

    @Column(name = "isbn", length = 13, unique = true)
    private String isbn;

    @Column(name = "thumbnail_key", length = 100)
    private String thumbnailKey;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "rating")
    private Double rating = 0.0;

    @Column(name = "review_count")
    private Long reviewCount = 0L;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public void update(String title, String author, String description, String publisher, LocalDate publishedDate) {
        if (title != null) this.title = title;
        if (author != null) this.author = author;
        if (description != null) this.description = description;
        if (publisher != null) this.publisher = publisher;
        if (publishedDate != null) this.publishedDate = publishedDate;
    }

    // 논리 삭제
    public void delete() {
        this.isDeleted = true;
    }
}
