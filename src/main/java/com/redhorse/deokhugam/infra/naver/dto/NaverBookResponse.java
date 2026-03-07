package com.redhorse.deokhugam.infra.naver.dto;

import java.util.List;

public record NaverBookResponse(
        List<NaverBookItem> items
) {
    public record NaverBookItem(
            String title,
            String author,
            String description,
            String publisher,
            String pubdate,
            String isbn,
            String image
    ) {}
}
