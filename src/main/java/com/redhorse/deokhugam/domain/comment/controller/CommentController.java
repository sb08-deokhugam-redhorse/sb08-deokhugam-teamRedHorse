package com.redhorse.deokhugam.domain.comment.controller;

import com.redhorse.deokhugam.domain.comment.dto.CommentCreateRequest;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import com.redhorse.deokhugam.domain.comment.dto.CommentUpdateRequest;
import com.redhorse.deokhugam.domain.comment.service.CommentService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

  private final CommentService commentService;

  @PostMapping
  public ResponseEntity<CommentDto> create(
      @Valid @RequestBody CommentCreateRequest commentCreateRequest) {
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
    CommentDto comment = commentService.update(commentId, requestUserId, commentUpdateRequest);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(comment);
  }

  @GetMapping("/{commentId}")
  public ResponseEntity<CommentDto> find(
      @PathVariable UUID commentId
  ) {
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
    commentService.hardDelete(commentId, requestUserId);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }
}
