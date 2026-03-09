package com.redhorse.deokhugam.domain.book.mapper;

import com.redhorse.deokhugam.domain.book.dto.response.BookDto;
import com.redhorse.deokhugam.domain.book.entity.Book;
import com.redhorse.deokhugam.infra.s3.S3ImageStorage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {S3ImageStorage.class})
public interface BookMapper
{
    // Long -> int, Double -> double
    @Mapping(target = "reviewCount", expression = "java(book.getReviewCount().intValue())")
    @Mapping(target = "rating", expression = "java(book.getRating() != null ? book.getRating() : 0.0)")
    @Mapping(target = "thumbnailUrl", qualifiedByName = "toPresignedUrl")
    BookDto toBookDto(Book book);
}
