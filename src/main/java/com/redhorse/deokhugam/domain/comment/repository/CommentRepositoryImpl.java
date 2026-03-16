package com.redhorse.deokhugam.domain.comment.repository;

import static com.redhorse.deokhugam.domain.comment.entity.QComment.comment;
import static com.redhorse.deokhugam.domain.user.entity.QUser.user;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.redhorse.deokhugam.domain.comment.dto.CommentPageRequest;
import com.redhorse.deokhugam.domain.comment.entity.Comment;
import com.redhorse.deokhugam.global.exception.InvalidCursorException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Comment> findAllByCursor(CommentPageRequest commentPageRequest) {

    boolean isAsc = "ASC".equalsIgnoreCase(commentPageRequest.direction());

    return queryFactory
        .selectFrom(comment)
        .join(comment.user, user).fetchJoin() // 댓글 목록 조회할 때 댓글 작성자의 정보까지 한 번에 조회하도록 함 (N+1 문제 방지)
        .where(
            comment.review.id.eq(commentPageRequest.reviewId()),
            comment.deletedAt.isNull(),
            combineCursorCondition(commentPageRequest.cursor(), commentPageRequest.after(), isAsc)
        )
        .orderBy(getOrderSpecifiers(isAsc))
        .limit(commentPageRequest.limit() + 1)
        .fetch();
  }

  private BooleanExpression combineCursorCondition(String cursor, Instant after, boolean isAsc) {
    if (cursor == null || after == null) {
      return null;
    }

    try {
      UUID cursorId = UUID.fromString(cursor);

      return isAsc
          ? comment.createdAt.gt(after).or(comment.createdAt.eq(after).and(comment.id.gt(cursorId)))
          : comment.createdAt.lt(after).or(comment.createdAt.eq(after).and(comment.id.lt(cursorId)));
    } catch (IllegalArgumentException e) {
      throw new InvalidCursorException("유효하지 않은 커서 형식입니다. " + cursor);
    }
  }

  private OrderSpecifier<?>[] getOrderSpecifiers(boolean isAsc) {
    return isAsc
        ? new OrderSpecifier[]{comment.createdAt.asc(), comment.id.asc()}
        : new OrderSpecifier[]{comment.createdAt.desc(), comment.id.desc()};
  }
}
