package com.redhorse.deokhugam.domain.comment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.redhorse.deokhugam.domain.book.entity.Book;
import com.redhorse.deokhugam.domain.book.repository.BookRepository;
import com.redhorse.deokhugam.domain.comment.dto.CommentPageRequest;
import com.redhorse.deokhugam.domain.comment.entity.Comment;
import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.domain.review.repository.ReviewRepository;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import com.redhorse.deokhugam.global.config.JpaConfig;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaConfig.class)
@DisplayName("CommentRepository 테스트")
class CommentRepositoryTest {

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private ReviewRepository reviewRepository;

  private User savedUser;
  private Book savedBook;
  private Review savedReview;

  @BeforeEach
  void setUp() {
    savedUser = new User("Buzz@codeit.com", "버즈", "Thisistest123***");
    userRepository.save(savedUser);

    savedBook = new Book("스프링 부트 핵심", "김스프링", "스프링 부트 마스터하기", "IT북스",
        LocalDate.of(2024, 1, 1), "9788900000001", "url1", false, 4.8, 10L, new ArrayList<>());
    bookRepository.save(savedBook);

    savedReview = new Review("너무 재밌어요", 5, savedBook, savedUser);
    reviewRepository.save(savedReview);
  }

  @Test
  @DisplayName("댓글 등록 성공")
  void create_Success() {
    // given
    Comment comment = new Comment("2회독 했습니다.", savedReview, savedUser);

    // when
    Comment result = commentRepository.save(comment);

    // then
    assertThat(result.getId()).isNotNull();
    assertThat(result.getContent()).isEqualTo("2회독 했습니다.");
    assertThat(result.getUser()).isEqualTo(savedUser);
    assertThat(result.getReview()).isEqualTo(savedReview);
  }

  @Test
  @DisplayName("댓글 조회 성공")
  void findById_Success() {
    // given
    Comment comment = new Comment("2회독 했습니다.", savedReview, savedUser);
    Comment savedComment = commentRepository.save(comment);

    // when
    Comment result = commentRepository.findByIdAndDeletedAtIsNull(savedComment.getId())
        .orElseThrow();

    // then
    assertThat(result.getContent()).isEqualTo("2회독 했습니다.");
    assertThat(result.getUser()).isEqualTo(savedUser);
  }

  @Test
  @DisplayName("댓글 수정 성공")
  void update_Success() {
    // given
    Comment comment = new Comment("2회독 했습니다.", savedReview, savedUser);
    Comment savedComment = commentRepository.save(comment);

    // when
    savedComment.update("수정된 댓글");
    commentRepository.flush();

    // then
    Comment updatedComment = commentRepository.findByIdAndDeletedAtIsNull(savedComment.getId())
        .orElseThrow();
    assertThat(updatedComment.getContent()).isEqualTo("수정된 댓글");
  }

  @Test
  @DisplayName("댓글 논리 삭제 성공")
  void softDelete_Success() {
    // given
    Comment comment = new Comment("2회독 했습니다.", savedReview, savedUser);
    Comment savedComment = commentRepository.save(comment);

    // when
    savedComment.softDelete();
    commentRepository.flush();

    // then
    Comment foundComment = commentRepository.findById(comment.getId()).orElseThrow();
    assertThat(foundComment.getDeletedAt()).isNotNull();
  }

  @Test
  @DisplayName("댓글 물리 삭제 성공")
  void hardDelete_Success() {
    // given
    Comment comment = new Comment("2회독 했습니다.", savedReview, savedUser);
    Comment savedComment = commentRepository.save(comment);

    // when
    commentRepository.delete(savedComment);
    commentRepository.flush();

    // then
    Optional<Comment> foundComment = commentRepository.findById(savedComment.getId());
    assertThat(foundComment).isEmpty();
  }

  @Test
  @DisplayName("댓글 목록 조회 성공(오름차) - 첫 페이지일 경우")
  void findAllByCursor_isAsc_Success() {
    // given
    for (int i = 1; i <= 10; i++) {
      commentRepository.save(new Comment(i + "번 댓글", savedReview, savedUser));
    }
    CommentPageRequest request = new CommentPageRequest(savedReview.getId(), "ASC", null, null, 5);

    // when
    List<Comment> result = commentRepository.findAllByCursor(request);

    // then
    assertThat(result).hasSize(6); // limit + 1만큼 조회되었는지 확인
    assertThat(result.get(0).getContent()).isEqualTo("1번 댓글"); // 오름차순으로 제일 처음에 등록된 댓글 확인
  }

  @Test
  @DisplayName("댓글 목록 조회 성공 - 첫 페이지일 경우")
  void findAllByCursor_FirstPage_Success() {
    // given
    for (int i = 1; i <= 10; i++) {
      commentRepository.save(new Comment(i + "번 댓글", savedReview, savedUser));
    }
    CommentPageRequest request = new CommentPageRequest(savedReview.getId(), null, null, null, 5);

    // when
    List<Comment> result = commentRepository.findAllByCursor(request);

    // then
    assertThat(result).hasSize(6); // limit + 1만큼 조회되었는지 확인
    assertThat(result.get(0).getContent()).isEqualTo("10번 댓글"); // 내림차순으로 제일 최근에 등록된 댓글 확인
  }

  @Test
  @DisplayName("댓글 목록 조회 성공 - 다음 페이지일 경우")
  void findAllByCursor_NextPage_Success() {
    // given
    for (int i = 1; i <= 10; i++) {
      commentRepository.save(new Comment(i + "번 댓글", savedReview, savedUser));
    }
    CommentPageRequest firstRequest = new CommentPageRequest(savedReview.getId(), null, null, null, 5);
    List<Comment> result = commentRepository.findAllByCursor(firstRequest); // 10번부터 6번까지 조회

    CommentPageRequest nextRequest = new CommentPageRequest(savedReview.getId(), null, result.get(4).getId().toString(), result.get(4).getCreatedAt(), 5);

    // when
    List<Comment> nextResult = commentRepository.findAllByCursor(nextRequest);

    // then
    List<String> contents = nextResult.stream().map(Comment::getContent).toList();
    assertThat(contents).containsExactly("5번 댓글", "4번 댓글", "3번 댓글", "2번 댓글", "1번 댓글");
    assertThat(nextResult.get(0).getContent()).isEqualTo("5번 댓글"); // 6번 다음인 5번부터 시작하는지
  }

  @Test
  @DisplayName("댓글 목록 조회 성공 - 잘못된 형식의 UUID 커서가 넘어온 경우")
  void findAllByCursor_InvalidUUID_ReturnFirstPage() {
    // given
    for (int i = 1; i <= 5; i++) {
      commentRepository.save(new Comment(i + "번 댓글", savedReview, savedUser));
    }

    // 커서 역할을 못하기 때문에 다음 페이지 조회 x
    CommentPageRequest request = new CommentPageRequest(savedReview.getId(), "invalid-uuid", Instant.now().toString(), null, 5);

    // when
    List<Comment> result = commentRepository.findAllByCursor(request);

    // then
    assertThat(result).hasSize(5);
    assertThat(result.get(0).getContent()).isEqualTo("5번 댓글"); // 최신순 첫 페이지 데이터
  }

  @Test
  @DisplayName("댓글 목록 조회 성공 - 댓글이 논리 삭제가 일어난 경우")
  void findAllByCursor_ExcludeDeleted_Success() {
    // given
    Comment comment1 = commentRepository.save(new Comment("살아있는 댓글", savedReview, savedUser));
    Comment comment2 = commentRepository.save(new Comment("삭제된 댓글", savedReview, savedUser));
    comment2.softDelete();
    commentRepository.flush();

    CommentPageRequest request = new CommentPageRequest(savedReview.getId(), null, null, null, 10);

    // when
    List<Comment> result = commentRepository.findAllByCursor(request);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getContent()).isEqualTo("살아있는 댓글");
  }
}