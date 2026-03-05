package com.redhorse.deokhugam.domain.book.service;

import com.redhorse.deokhugam.domain.book.dto.request.BookCreateRequest;
import com.redhorse.deokhugam.domain.book.dto.request.BookUpdateRequest;
import com.redhorse.deokhugam.domain.book.dto.response.BookDto;
import com.redhorse.deokhugam.domain.book.entity.Book;
import com.redhorse.deokhugam.domain.book.exception.BookNotFoundException;
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
import java.util.Optional;
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
    private BookUpdateRequest bookUpdateRequest;

    private Book book;
    private BookDto bookDto;
    private UUID bookId;

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

        bookUpdateRequest = new BookUpdateRequest(
                "수정된 자바 프로그래밍",
                "수정된 김자바",
                "수정된 소개",
                "수정된 출판사",
                LocalDate.of(2025, 1, 1)
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

        bookId = UUID.randomUUID();
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
    
    @Nested
    @DisplayName("도서 수정")
    class Update {
        @Test
        @DisplayName("성공 - 유효한 요청이면 도서가 수정된다.")
        void success_withValidRequest_updatesBook() {
            // given
            given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
            given(bookMapper.toBookDto(book)).willReturn(bookDto);
            
            // when
            BookDto result = bookServiceimpl.update(bookId, bookUpdateRequest, null);
            
            // then
            assertThat(result).isNotNull();
            assertThat(book.getTitle()).isEqualTo("수정된 자바 프로그래밍");
            assertThat(book.getAuthor()).isEqualTo("수정된 김자바");
            assertThat(book.getPublisher()).isEqualTo("수정된 출판사");

            then(bookRepository).should(times(1)).findById(bookId);
            then(bookMapper).should(times(1)).toBookDto(book);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 도서면 BookNotFoundException이 발생한다.")
        void fail_withNonExistentBook_throwsBookNotFoundException() {
            // given
            given(bookRepository.findById(bookId)).willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> bookServiceimpl.update(bookId, bookUpdateRequest, null))
                    .isInstanceOf(BookNotFoundException.class);

            // then
            then(bookRepository).should().findById(bookId);
            then(bookRepository).should(never()).save(any(Book.class));
        }
    }

    @Nested
    @DisplayName("도서 삭제")
    class SoftDelete {
        @Test
        @DisplayName("성공 - 도서를 논리 삭제한다.")
        void success_softDelete() {
            // given
            given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

            // when
            bookServiceimpl.softDelete(bookId);

            // then
            assertThat(book.getIsDeleted()).isTrue();
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 도서면 BookNotFoundException이 발생한다.")
        void fail_withNonExistentBook_throwsBookNotFoundException() {
            // given
            given(bookRepository.findById(bookId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> bookServiceimpl.softDelete(bookId))
                    .isInstanceOf(BookNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("hardDelete 메서드")
    class HardDelete {

        @Test
        @DisplayName("성공 - 도서를 물리 삭제한다")
        void success_hardDelete() {
            // given
            given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

            // when
            bookServiceimpl.hardDelete(bookId);

            // then
            then(bookRepository).should().delete(book);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 도서면 BookNotFoundException을 던진다")
        void fail_withNonExistentBook_throwsBookNotFoundException() {
            // given
            given(bookRepository.findById(bookId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> bookServiceimpl.hardDelete(bookId))
                    .isInstanceOf(BookNotFoundException.class);
        }
    }
}