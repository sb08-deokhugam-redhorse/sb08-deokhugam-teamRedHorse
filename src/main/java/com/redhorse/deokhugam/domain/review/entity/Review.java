package com.redhorse.deokhugam.domain.review.entity;

import com.redhorse.deokhugam.domain.book.entity.Book;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.global.entity.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@Table(name = "reviews", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_book_user_id",
                columnNames = {"book_id", "user_id"}
        )
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseUpdatableEntity {
    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "likes", nullable = false)
    private Long likes = 0L;

    @Column(name = "comments", nullable = false)
    private Long comments = 0L;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_book_id"))
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_reviews_user_id"))
    private User user;

    public Review(int rating, String content, Book book, User user) {
        this.rating = rating;
        this.content = content;
        this.likes = 0L;
        this.comments = 0L;
        this.book = book;
        this.user = user;
    }
}
