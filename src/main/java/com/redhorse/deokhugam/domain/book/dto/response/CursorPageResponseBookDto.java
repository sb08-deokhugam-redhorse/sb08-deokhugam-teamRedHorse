package com.redhorse.deokhugam.domain.book.dto.response;

import java.time.Instant;
import java.util.List;

public record CursorPageResponseBookDto(
        List<BookDto> content,
        String nextCursor,
        Instant nextAfter,
        int size,
        long totalElements,
        boolean hasNext
) {}
