package com.redhorse.deokhugam.domain.book.controller;

import com.redhorse.deokhugam.domain.book.dto.request.BookCreateRequest;
import com.redhorse.deokhugam.domain.book.dto.response.BookDto;
import com.redhorse.deokhugam.domain.book.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BookController
{
    private final BookService bookService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookDto> createBook(@Valid @RequestPart("bookData") BookCreateRequest bookCreateRequest,
                                              @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage)
    {
        log.debug("책 생성 시작(정보): bookName={}", bookCreateRequest.title());

        BookDto book = bookService.create(bookCreateRequest, thumbnailImage);

        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }
}
