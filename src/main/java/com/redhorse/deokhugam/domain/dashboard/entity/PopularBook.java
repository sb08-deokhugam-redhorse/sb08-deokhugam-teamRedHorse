package com.redhorse.deokhugam.domain.dashboard.entity;

import com.redhorse.deokhugam.domain.book.entity.Book;
import com.redhorse.deokhugam.global.entity.BaseEntity;
import com.redhorse.deokhugam.global.entity.PeriodType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "popular_books")
@Entity
public class PopularBook extends BaseEntity
{
    @Enumerated(EnumType.STRING)
    @Column(name = "period", length = 20, nullable = false)
    private PeriodType period = PeriodType.DAILY;

    @Column(name = "rating", nullable = false)
    private Double rating = 0.0;

    @Column(name = "score", nullable = false)
    private Long score = 0L;

    @Column(name = "review_count", nullable = false)
    private Long reviewCount = 0L;

    @Column(name = "ranking", nullable = false)
    private Long ranking = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_popular_books_books"))
    private Book book;

    public PopularBook(PeriodType period, Double rating, Long score, Long reviewCount, Long ranking, Book book) {
        this.period = period;
        this.rating = rating;
        this.score = score;
        this.reviewCount = reviewCount;
        this.ranking = ranking;
        this.book = book;
    }
}
