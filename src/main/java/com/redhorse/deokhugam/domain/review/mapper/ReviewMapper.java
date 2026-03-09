package com.redhorse.deokhugam.domain.review.mapper;

import com.redhorse.deokhugam.domain.review.dto.ReviewDto;
import com.redhorse.deokhugam.domain.review.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

  @Mapping(source = "book.id", target = "bookId")
  @Mapping(source = "book.title", target = "bookTitle")
  @Mapping(source = "book.thumbnailUrl", target = "bookThumbnailUrl")
  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "user.nickname", target = "userNickname")
  @Mapping(target = "likedByMe", ignore = true)
  ReviewDto toDto(Review review);

  @Mapping(source = "review.book.id", target = "bookId")
  @Mapping(source = "review.book.title", target = "bookTitle")
  @Mapping(source = "review.book.thumbnailUrl", target = "bookThumbnailUrl")
  @Mapping(source = "review.user.id", target = "userId")
  @Mapping(source = "review.user.nickname", target = "userNickname")
  @Mapping(source = "likedByMe", target = "likedByMe")
  @Mapping(source="commentCount",target="commentCount")
  ReviewDto toDto(Review review, boolean likedByMe, int commentCount);
}
