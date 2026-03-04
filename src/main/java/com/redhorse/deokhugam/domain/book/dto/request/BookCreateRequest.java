package com.redhorse.deokhugam.domain.book.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record BookCreateRequest(
        @Size(max = 100)
        @NotBlank(message = "제목은 필수 입력 사항입니다.")
        String title,

        @Size(max = 50)
        @NotBlank(message = "저자는 필수 입력 사항입니다.")
        String author,

        @Size(max = 3000)
        @NotBlank(message = "소개는 필수 입력 사항입니다.")
        String description,

        @Size(max = 50)
        @NotBlank(message = "출판사는 필수 입력 사항입니다.")
        String publisher,

        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate publishedDate,

        @Pattern(regexp = "^(\\d{9}[\\dXx]|97[89]\\d{10})$", message = "유효한 ISBN 형식이 아닙니다.")
        String isbn
) {}
