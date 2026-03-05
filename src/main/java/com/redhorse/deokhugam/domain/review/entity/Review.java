package com.redhorse.deokhugam.domain.review.entity;

import com.redhorse.deokhugam.domain.book.entity.Book;
import com.redhorse.deokhugam.domain.comment.entity.Comment;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularReview;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.global.entity.BaseUpdatableEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
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
    private Long likeCount = 0L;

    @Column(name = "comments", nullable = false)
    private Long commentCount = 0L;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_book_id"))
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_reviews_user_id"))
    private User user;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<ReviewLike> reviewLikes = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<PopularReview> popularReviews = new ArrayList<>();

    public Review(String content, int rating, Book book, User user) {
        this.content = content;
        this.rating = rating;
        this.likeCount = 0L;
        this.commentCount = 0L;
        this.book = book;
        this.user = user;
    }

    public void update(String content, Integer rating){
        if(content != null && !content.equals(this.content)){
            this.content = content;
        }
        if(rating != null && rating != this.rating){
            this.rating = rating;
        }
    }

    public void delete(){
        this.deletedAt = Instant.now();
    }
}
