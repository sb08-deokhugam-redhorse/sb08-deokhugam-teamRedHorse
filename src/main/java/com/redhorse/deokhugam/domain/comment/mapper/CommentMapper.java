package com.redhorse.deokhugam.domain.comment.mapper;

import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import com.redhorse.deokhugam.domain.comment.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

  @Mapping(target = "reviewId", source = "review.id")
  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "userNickname", source = "user.nickname")
  CommentDto toDto(Comment comment);
}
