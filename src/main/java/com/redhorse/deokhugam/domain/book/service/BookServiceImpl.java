package com.redhorse.deokhugam.domain.book.service;

import com.redhorse.deokhugam.domain.book.dto.request.BookCreateRequest;
import com.redhorse.deokhugam.domain.book.dto.request.BookUpdateRequest;
import com.redhorse.deokhugam.domain.book.dto.response.BookDto;
import com.redhorse.deokhugam.domain.book.dto.response.CursorPageResponseBookDto;
import com.redhorse.deokhugam.domain.book.entity.Book;
import com.redhorse.deokhugam.domain.book.exception.BookNotFoundException;
import com.redhorse.deokhugam.domain.book.exception.IsbnDuplicateException;
import com.redhorse.deokhugam.domain.book.mapper.BookMapper;
import com.redhorse.deokhugam.domain.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService
{
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    /**
     * 도서를 등록한다.
     * <p>
     *      ISBN이 제공된 경우 중복 여부를 검사하며, 썸네일 이미지는 S3에 저장한다.
     * </p>
     *
     * @param bookCreateRequest 도서 등록 요청 정보
     * @param thumbnailImage    썸네일 이미지 파일
     * @return 등록된 도서 정보
     * @throws IsbnDuplicateException ISBN이 이미 존재하는 경우
     */
    @Transactional
    @Override
    public BookDto create(BookCreateRequest bookCreateRequest, MultipartFile thumbnailImage) {
        if (bookCreateRequest.isbn() != null && bookRepository.existsByIsbn(bookCreateRequest.isbn())) {
            throw new IsbnDuplicateException(bookCreateRequest.isbn());
        }

        String thumbnailUrl = (thumbnailImage != null && !thumbnailImage.isEmpty())
                ? thumbnailImage.getOriginalFilename()
                : null;

        Book book = new Book(
                bookCreateRequest.title(),
                bookCreateRequest.author(),
                bookCreateRequest.description(),
                bookCreateRequest.publisher(),
                bookCreateRequest.publishedDate(),
                bookCreateRequest.isbn(),
                thumbnailUrl,
                false,
                0.0,
                0L,
                new ArrayList<>()
        );

        Book savedBook = bookRepository.save(book);

        log.info("[Book-Controller] 등록 작업 완료: book={}", savedBook);

        return bookMapper.toBookDto(savedBook);
    }

    /**
     * 도서 목록을 커서 페이지네이션으로 조회한다.
     *
     * @param keyword   검색 키워드 (제목, 저자, ISBN)
     * @param orderBy   정렬 기준 (title, publishedDate, rating, reviewCount)
     * @param direction 정렬 방향 (ASC, DESC)
     * @param cursor    이전 페이지 마지막 요소의 정렬 기준 값 (1차 커서)
     * @param after     이전 페이지 마지막 요소의 createdAt (2차 커서)
     * @param limit     페이지 크기
     * @return 도서 목록과 다음 페이지 존재 여부
     */
    @Override
    public CursorPageResponseBookDto getBooks(String keyword, String orderBy, String direction, String cursor, Instant after, int limit) {
        if (limit < 1) {
            throw new IllegalArgumentException("limit 값은 반드시 0보다 커야 합니다.");
        }
        Slice<Book> slice = bookRepository.getAllBooks(keyword, orderBy, direction, cursor, after, limit);

        List<Book> books = slice.getContent();
        long totalElements = bookRepository.countBooksWithKeyword(keyword);

        String nextCursor = null;
        Instant nextAfter = null;

        if (slice.hasNext() && !books.isEmpty()) {
            Book lastBook = books.get(books.size() - 1);
            nextCursor = resolveNextCursor(orderBy, lastBook);
            nextAfter = lastBook.getCreatedAt();
        }

        List<BookDto> content = books.stream()
                .map(bookMapper::toBookDto)
                .toList();

        return new CursorPageResponseBookDto(
                content,
                nextCursor,
                nextAfter,
                content.size(),
                totalElements,
                slice.hasNext()
        );
    }

    /**
     * 도서 ID로 상세 정보를 조회한다.
     *
     * @param bookId 조회할 도서 ID
     * @return 도서 정보
     */
    @Override
    public BookDto findById(UUID bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        log.info("[Book-Service] 단건 조회 작업 완료: bookId={}", bookId);

         return bookMapper.toBookDto(book);
    }

    /**
     * 도서 정보를 수정한다.
     * <p>
     *      제목, 저자, 소개, 출판사, 출간일만 수정 가능하다.
     * </p>
     *
     * @param bookId            수정할 도서 ID
     * @param bookUpdateRequest 도서 수정 요청 정보
     * @param thumbnailImage    썸네일 이미지 파일
     * @return 수정된 도서 정보
     * @throws BookNotFoundException 도서를 찾을 수 없는 경우
     */
    @Transactional
    @Override
    public BookDto update(UUID bookId, BookUpdateRequest bookUpdateRequest, MultipartFile thumbnailImage) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        book.update(
                bookUpdateRequest.title(),
                bookUpdateRequest.author(),
                bookUpdateRequest.description(),
                bookUpdateRequest.publisher(),
                bookUpdateRequest.publishedDate()
        );

        log.info("[Book-Service] 수정 작업 완료: book={}", book);

        return bookMapper.toBookDto(book);
    }

    /**
     * 도서를 논리 삭제한다.
     * <p>
     *      삭제 후에도 관련 리뷰, 댓글 등의 데이터는 DB에 유지된다.
     * </p>
     *
     * @param bookId 논리 삭제할 도서 ID
     * @throws BookNotFoundException 도서를 찾을 수 없는 경우
     */
    @Transactional
    @Override
    public void softDelete(UUID bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        book.delete();

        log.info("[Book-Service] 논리 삭제 작업 완료: bookId={}", bookId);
    }

    /**
     * 도서를 물리 삭제한다.
     * <p>
     *     도서와 관련된 리뷰, 댓글 등 모든 데이터가 DB에서 영구 삭제된다.
     *     UI로 제공되지 않으며 테스트 코드를 통해서만 검증한다.
     * </p>
     *
     * @param bookId 물리 삭제할 도서 ID
     * @throws BookNotFoundException 도서를 찾을 수 없는 경우
     */
    @Transactional
    @Override
    public void hardDelete(UUID bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        bookRepository.delete(book);

        log.info("[Book-Service] 물리 삭제 작업 완료: bookId={}", bookId);
    }

    @Override
    public String extractIsbnFromImage(MultipartFile file) {
        return "";
    }

    /**
     * 정렬 기준에 따라 마지막 요소의 값을 nextCursor 문자열로 변환한다.
     *
     * @param orderBy  정렬 기준
     * @param lastBook 마지막 요소
     * @return nextCursor 문자열
     */
    private String resolveNextCursor(String orderBy, Book lastBook) {
        return switch (orderBy == null ? "title" : orderBy) {
            case "publishedDate" -> lastBook.getPublishedDate().toString();
            case "rating" -> lastBook.getRating().toString();
            case "reviewCount" -> lastBook.getReviewCount().toString();
            default -> lastBook.getTitle();
        };
    }
}
