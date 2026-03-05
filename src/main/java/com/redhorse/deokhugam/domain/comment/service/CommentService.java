package com.redhorse.deokhugam.domain.comment.service;

import com.redhorse.deokhugam.domain.comment.dto.CommentCreateRequest;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import com.redhorse.deokhugam.domain.comment.dto.CommentUpdateRequest;
import java.util.UUID;

public interface CommentService {

  // 댓글 등록
  CommentDto create(CommentCreateRequest commentCreateRequest);

  // 댓글 수정
  CommentDto update(UUID commentId, UUID requestUserId, CommentUpdateRequest commentUpdateRequest);

  // 댓글 상세 조회
  CommentDto find(UUID commentId);

  // 댓글 목록 조회

  // 댓글 논리 삭제
  void softDelete(UUID commentId, UUID requestUserId);

  // 댓글 물리 삭제
  void hardDelete(UUID commentId, UUID requestUserId);
}
