package com.redhorse.deokhugam.global.batch.repository;

import com.redhorse.deokhugam.domain.book.entity.Book;
import com.redhorse.deokhugam.domain.dashboard.dto.popularbook.BookBatchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface BookBatchRepository extends JpaRepository<Book, UUID> {
    @Query("SELECT new com.redhorse.deokhugam.domain.dashboard.dto.popularbook.BookBatchDto(" +
            "CAST(:period AS string), r.id, r.reviewCount,  r.rating, CAST(r.reviewCount * 0.4 + r.rating * 0.6 AS double)) " +
            "FROM Book r " +
            "WHERE r.createdAt >= :startDay " +
            "AND r.createdAt < :endDay " +
            "AND r.isDeleted = false " +
            "ORDER BY (r.reviewCount * 0.4 + r.rating * 0.6) DESC")
    Page<BookBatchDto> findBooks(
            @Param("period") String period,
            @Param("startDay") Instant startDay,
            @Param("endDay") Instant endDay,
            Pageable pageable
    );
}
