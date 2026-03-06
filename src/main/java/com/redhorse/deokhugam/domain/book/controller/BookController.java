package com.redhorse.deokhugam.domain.book.controller;

import com.redhorse.deokhugam.domain.book.dto.request.BookCreateRequest;
import com.redhorse.deokhugam.domain.book.dto.request.BookUpdateRequest;
import com.redhorse.deokhugam.domain.book.dto.response.BookDto;
import com.redhorse.deokhugam.domain.book.dto.response.CursorPageResponseBookDto;
import com.redhorse.deokhugam.domain.book.service.BookService;
import com.redhorse.deokhugam.infra.naver.NaverBookProvider;
import com.redhorse.deokhugam.infra.naver.dto.NaverBookDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BookController
{
    private final BookService bookService;
    private final NaverBookProvider naverBookProvider;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookDto> createBook(@Valid @RequestPart("bookData") BookCreateRequest bookCreateRequest,
                                              @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage)
    {
        log.debug("[Book-Controller] 생성 요청 시작: bookName={}", bookCreateRequest.title());

        BookDto book = bookService.create(bookCreateRequest, thumbnailImage);

        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookDto> getBookById(@PathVariable("bookId") UUID bookId) {
        log.debug("[Book-Controller] 단건 조회 요청 시작: bookId={}", bookId);

        BookDto book = bookService.findById(bookId);

        return ResponseEntity.status(HttpStatus.OK).body(book);
    }

    @GetMapping
    public ResponseEntity<CursorPageResponseBookDto> getAllBooks(@RequestParam(required = false) String keyword,
                                                                 @RequestParam(required = false, defaultValue = "title") String orderBy,
                                                                 @RequestParam(required = false, defaultValue = "DESC") String direction,
                                                                 @RequestParam(required = false) String cursor,
                                                                 @RequestParam(required = false) Instant after,
                                                                 @RequestParam(required = false, defaultValue = "50") int limit)
    {
        log.debug("[Book-Controller] 다건 조회 요청 시작: keyword={}, orderBy={}, direction={}, cursor={}, after={}", keyword, orderBy, direction, cursor, after);

        CursorPageResponseBookDto response = bookService.getBooks(keyword, orderBy, direction, cursor, after, limit);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{bookId}")
    public ResponseEntity<BookDto> updateBook(@PathVariable UUID bookId,
                                              @Valid @RequestPart("bookData") BookUpdateRequest bookUpdateRequest,
                                              @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage)
    {
        log.debug("[Book-Controller] 수정 요청 시작: bookId={}", bookId);

        BookDto book = bookService.update(bookId, bookUpdateRequest, thumbnailImage);

        return ResponseEntity.status(HttpStatus.OK).body(book);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> softDeleteBook(@PathVariable UUID bookId) {
        log.debug("[Book-Controller] 논리 삭제 요청 시작: bookId={}", bookId);

        bookService.softDelete(bookId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/info")
    public ResponseEntity<NaverBookDto> getBookInfo(@RequestParam String isbn) {
        log.debug("[Book-Controller] 도서 정보 조회 요청 시작: isbn={}", isbn);

        NaverBookDto bookInfo = naverBookProvider.getBookInfoByIsbn(isbn);

        return ResponseEntity.status(HttpStatus.OK).body(bookInfo);
    }

    @DeleteMapping("/{bookId}/hard")
    public ResponseEntity<Void> hardDeleteBook(@PathVariable UUID bookId) {
        log.debug("[Book-Controller] 물리 삭제 요청 시작: bookId={}", bookId);

        bookService.hardDelete(bookId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
