package com.redhorse.deokhugam.domain.book.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class BookNotFoundException extends BookException
{
    public BookNotFoundException(UUID bookId) {
        super(ErrorCode.BOOK_NOT_FOUND, Map.of("bookId", bookId));
    }
}
