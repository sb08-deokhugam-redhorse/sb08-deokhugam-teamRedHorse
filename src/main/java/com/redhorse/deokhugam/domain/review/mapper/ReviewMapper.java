package com.redhorse.deokhugam.domain.review.mapper;

import com.redhorse.deokhugam.domain.review.dto.ReviewDto;
import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.infra.s3.S3ImageStorage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {S3ImageStorage.class})
public interface ReviewMapper {

  @Mapping(source = "book.id", target = "bookId")
  @Mapping(source = "book.title", target = "bookTitle")
  @Mapping(source = "review.book.thumbnailUrl", target = "bookThumbnailUrl", qualifiedByName = "toPresignedUrl")
  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "user.nickname", target = "userNickname")
  @Mapping(target = "likedByMe", ignore = true)
  ReviewDto toDto(Review review);

  @Mapping(source = "review.book.id", target = "bookId")
  @Mapping(source = "review.book.title", target = "bookTitle")
  @Mapping(source = "review.book.thumbnailUrl", target = "bookThumbnailUrl", qualifiedByName = "toPresignedUrl")
  @Mapping(source = "review.user.id", target = "userId")
  @Mapping(source = "review.user.nickname", target = "userNickname")
  @Mapping(source = "likedByMe", target = "likedByMe")
  ReviewDto toDto(Review review, boolean likedByMe);
}
