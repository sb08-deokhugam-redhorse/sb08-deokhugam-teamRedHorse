package com.redhorse.deokhugam.domain.comment.repository;

import com.redhorse.deokhugam.domain.comment.dto.CommentPageRequest;
import com.redhorse.deokhugam.domain.comment.entity.Comment;
import java.util.List;

public interface CommentRepositoryCustom {

  List<Comment> findAllByCursor(CommentPageRequest commentPageRequest);
}
