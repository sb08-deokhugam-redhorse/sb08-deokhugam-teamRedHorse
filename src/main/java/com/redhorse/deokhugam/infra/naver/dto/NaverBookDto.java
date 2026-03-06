package com.redhorse.deokhugam.infra.naver.dto;

import java.time.LocalDate;

public record NaverBookDto(
        String title,
        String author,
        String publisher,
        String description,
        LocalDate publishedDate,
        String isbn,
        String thumbnailImage
) {}
