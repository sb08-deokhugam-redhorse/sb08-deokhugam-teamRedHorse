package com.redhorse.deokhugam.domain.book.service;

import com.redhorse.deokhugam.domain.book.dto.request.BookCreateRequest;
import com.redhorse.deokhugam.domain.book.dto.request.BookUpdateRequest;
import com.redhorse.deokhugam.domain.book.dto.response.BookDto;
import com.redhorse.deokhugam.domain.book.dto.response.CursorPageResponseBookDto;
import com.redhorse.deokhugam.domain.book.entity.Book;
import com.redhorse.deokhugam.domain.book.exception.IsbnDuplicateException;
import com.redhorse.deokhugam.domain.book.mapper.BookMapper;
import com.redhorse.deokhugam.domain.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService
{
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Transactional
    @Override
    public BookDto create(BookCreateRequest bookCreateRequest, MultipartFile thumbnailImage) {
        log.debug("책 등록 요청 - bookCreateRequest: {}", bookCreateRequest);

        if (bookCreateRequest.isbn() != null && bookRepository.existsByIsbn(bookCreateRequest.isbn())) {
            throw new IsbnDuplicateException(bookCreateRequest.isbn());
        }

        Book book = new Book(
                bookCreateRequest.title(),
                bookCreateRequest.author(),
                bookCreateRequest.description(),
                bookCreateRequest.publisher(),
                bookCreateRequest.publishedDate(),
                bookCreateRequest.isbn(),
                null,
                false,
                0.0,
                0L,
                new ArrayList<>()
        );

        Book savedBook = bookRepository.save(book);

        return bookMapper.toBookDto(savedBook);
    }

    @Override
    public CursorPageResponseBookDto getBooks(String keyword, String orderBy, String direction, Instant after, int limit) {
        return null;
    }

    @Override
    public BookDto findById(UUID bookId) {
        return null;
    }

    @Override
    public BookDto update(BookUpdateRequest bookUpdateRequest, MultipartFile thumbnailImage) {
        return null;
    }

    @Override
    public void softDelete(UUID bookId) {

    }

    @Override
    public void hardDelete(UUID bookId) {

    }

    @Override
    public String extractIsbnFromImage(MultipartFile file) {
        return "";
    }
}
