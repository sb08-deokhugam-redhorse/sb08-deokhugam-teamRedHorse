package com.redhorse.deokhugam.domain.comment.service;

import com.redhorse.deokhugam.domain.comment.dto.CommentCreateRequest;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;

public interface CommentService {

  // 댓글 등록
  CommentDto create(CommentCreateRequest commentCreateRequest);

  // 댓글 수정

  // 댓글 상세 조회

  // 댓글 목록 조회

  // 댓글 논리 삭제

  // 댓글 물리 삭제

}
