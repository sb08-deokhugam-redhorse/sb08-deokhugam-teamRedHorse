package com.redhorse.deokhugam.domain.comment.controller;

import com.redhorse.deokhugam.domain.comment.dto.CommentCreateRequest;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import com.redhorse.deokhugam.domain.comment.dto.CommentPageRequest;
import com.redhorse.deokhugam.domain.comment.dto.CommentUpdateRequest;
import com.redhorse.deokhugam.domain.comment.dto.CursorPageResponseCommentDto;
import com.redhorse.deokhugam.domain.comment.service.CommentService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

  private final CommentService commentService;

  @PostMapping
  public ResponseEntity<CommentDto> create(
      @Valid @RequestBody CommentCreateRequest commentCreateRequest) {
    log.info("[Comment-Controller] 생성 요청 시작: reviewId={}, userId={}",
        commentCreateRequest.reviewId(), commentCreateRequest.userId());

    CommentDto comment = commentService.create(commentCreateRequest);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(comment);
  }

  @PatchMapping("/{commentId}")
  public ResponseEntity<CommentDto> update(
      @PathVariable UUID commentId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId,
      @Valid @RequestBody CommentUpdateRequest commentUpdateRequest
  ) {
    log.info("[Comment-Controller] 수정 요청 시작: commentId={}, requestUserId={}", commentId,
        requestUserId);

    CommentDto comment = commentService.update(commentId, requestUserId, commentUpdateRequest);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(comment);
  }

  @GetMapping("/{commentId}")
  public ResponseEntity<CommentDto> find(
      @PathVariable UUID commentId
  ) {
    log.debug("[Comment-Controller] 단건 조회 요청 시작: commentId={}", commentId);

    CommentDto comment = commentService.find(commentId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(comment);
  }

  @DeleteMapping("/{commentId}")
  public ResponseEntity<Void> softDelete(
      @PathVariable UUID commentId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId
  ) {
    log.info("[Comment-Controller] 논리 삭제 요청 시작: commentId={}, requestUserId={}", commentId,
        requestUserId);
    commentService.softDelete(commentId, requestUserId);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @DeleteMapping("/{commentId}/hard")
  public ResponseEntity<Void> hardDelete(
      @PathVariable UUID commentId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId
  ) {
    log.info("[Comment-Controller] 물리 삭제 요청 시작: commentId={}, requestUserId={}", commentId,
        requestUserId);

    commentService.hardDelete(commentId, requestUserId);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @GetMapping
  public ResponseEntity<CursorPageResponseCommentDto> findAll(
      @Valid @ModelAttribute @ParameterObject CommentPageRequest commentPageRequest) {
    log.debug(
        "[Comment-Controller] 다건 조회 요청 시작 - reviewId: {}, cursor: {}, after: {}, limit: {}, direction: {}",
        commentPageRequest.reviewId(),
        commentPageRequest.cursor(),
        commentPageRequest.after(),
        commentPageRequest.limit(),
        commentPageRequest.direction());

    CursorPageResponseCommentDto commentDto = commentService.findAll(commentPageRequest);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(commentDto);
  }
}
