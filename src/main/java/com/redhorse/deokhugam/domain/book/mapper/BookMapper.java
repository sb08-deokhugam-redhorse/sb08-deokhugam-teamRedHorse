package com.redhorse.deokhugam.domain.book.mapper;

import com.redhorse.deokhugam.domain.book.dto.response.BookDto;
import com.redhorse.deokhugam.domain.book.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookMapper
{
    // Long -> int, Double -> double
    @Mapping(target = "reviewCount", expression = "java(book.getReviewCount().intValue())")
    @Mapping(target = "rating", expression = "java(book.getRating() != null ? book.getRating() : 0.0)")
    BookDto toBookDto(Book book);
}
