package com.redhorse.deokhugam.domain.book.service;

import com.redhorse.deokhugam.domain.book.dto.request.BookCreateRequest;
import com.redhorse.deokhugam.domain.book.dto.request.BookUpdateRequest;
import com.redhorse.deokhugam.domain.book.dto.response.BookDto;
import com.redhorse.deokhugam.domain.book.dto.response.CursorPageResponseBookDto;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.UUID;

public interface BookService
{
    BookDto create(BookCreateRequest bookCreateRequest, MultipartFile thumbnailImage);
    CursorPageResponseBookDto getBooks(String keyword, String orderBy, String direction, Instant after, int limit);
    BookDto findById(UUID bookId);
    BookDto update(BookUpdateRequest bookUpdateRequest, MultipartFile thumbnailImage);
    void softDelete(UUID bookId);
    void hardDelete(UUID bookId);
    String extractIsbnFromImage(MultipartFile file);
}
