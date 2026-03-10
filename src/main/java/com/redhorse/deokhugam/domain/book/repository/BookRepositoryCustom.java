package com.redhorse.deokhugam.domain.book.repository;

import com.redhorse.deokhugam.domain.book.entity.Book;
import org.springframework.data.domain.Slice;

import java.time.Instant;

public interface BookRepositoryCustom
{
    Slice<Book> getAllBooks(String keyword, String orderBy, String direction, String cursor, Instant after, int limit);
    long countBooksWithKeyword(String keyword);
}
