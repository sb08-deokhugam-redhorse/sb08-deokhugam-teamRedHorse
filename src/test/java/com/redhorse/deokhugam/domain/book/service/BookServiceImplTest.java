package com.redhorse.deokhugam.domain.book.service;

import com.redhorse.deokhugam.domain.book.dto.request.BookCreateRequest;
import com.redhorse.deokhugam.domain.book.dto.response.BookDto;
import com.redhorse.deokhugam.domain.book.entity.Book;
import com.redhorse.deokhugam.domain.book.exception.IsbnDuplicateException;
import com.redhorse.deokhugam.domain.book.mapper.BookMapper;
import com.redhorse.deokhugam.domain.book.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService Unit Test")
class BookServiceImplTest
{
    @InjectMocks private BookServiceImpl bookServiceimpl;
    @Mock private BookRepository bookRepository;
    @Mock private BookMapper bookMapper;

    private BookCreateRequest bookCreateRequest;
    private Book book;
    private BookDto bookDto;

    @BeforeEach
    void setUp() {
        bookCreateRequest = new BookCreateRequest(
                "자바 프로그래밍",
                "김자바",
                "자바 소개",
                "출판사A",
                LocalDate.of(2024, 1, 1),
                "9788965745464"
        );

        book = new Book(
                "자바 프로그래밍", "김자바", "자바 소개", "출판사A",
                LocalDate.of(2024, 1, 1), "9788965745464",
                null, false, 0.0, 0L, new ArrayList<>()
        );

        bookDto = new BookDto(
                UUID.randomUUID(), "자바 프로그래밍", "김자바", "자바 소개", "출판사A",
                LocalDate.of(2024, 1, 1), "9788965745464", null, 0, 0.0,
                Instant.now(), Instant.now()
        );
    }

    @Nested
    @DisplayName("도서 등록")
    class Create {
        @Test
        @DisplayName("성공 - ISBN이 있고 중복되지 않으면 도서가 등록된다.")
        void success_withValidIsbn_createsBook() {
            // given
            given(bookRepository.existsByIsbn("9788965745464")).willReturn(false);
            given(bookRepository.save(any(Book.class))).willReturn(book);
            given(bookMapper.toBookDto(book)).willReturn(bookDto);

            // when
            BookDto result = bookServiceimpl.create(bookCreateRequest, null);

            // then
            assertThat(result.title()).isEqualTo("자바 프로그래밍");
            then(bookRepository).should(times(1)).existsByIsbn("9788965745464");
            then(bookRepository).should(times(1)).save(any(Book.class));
        }

        @Test
        @DisplayName("성공 - ISBN없이 도서가 등록된다.")
        void success_withNoIsbn_createsBookWithoutIsbnCheck() {
            // given
            BookCreateRequest requestWithoutIsbn = new BookCreateRequest(
                    "자바 프로그래밍", "김자바", "자바 소개", "출판사A",
                    LocalDate.of(2024, 1, 1), null
            );
            given(bookRepository.save(any(Book.class))).willReturn(book);
            given(bookMapper.toBookDto(book)).willReturn(bookDto);

            // when
            BookDto result = bookServiceimpl.create(requestWithoutIsbn, null);

            // then
            assertThat(result).isNotNull();
            then(bookRepository).should(never()).existsByIsbn(any());
            then(bookRepository).should(times(1)).save(any(Book.class));
        }

        @Test
        @DisplayName("실패 - ISBN이 중복되면 IsbnDuplicateException이 발생한다.")
        void fail_withDuplicateIsbn_throwsIsbnDuplicateException() {
            // given
            given(bookRepository.existsByIsbn("9788965745464")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> bookServiceimpl.create(bookCreateRequest, null))
                    .isInstanceOf(IsbnDuplicateException.class);
            then(bookRepository).should(never()).save(any(Book.class));
        }
    }
}