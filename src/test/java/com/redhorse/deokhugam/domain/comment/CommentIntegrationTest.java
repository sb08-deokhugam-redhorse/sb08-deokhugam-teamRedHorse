package com.redhorse.deokhugam.domain.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhorse.deokhugam.domain.alarm.service.AlarmService;
import com.redhorse.deokhugam.domain.book.entity.Book;
import com.redhorse.deokhugam.domain.book.repository.BookRepository;
import com.redhorse.deokhugam.domain.comment.dto.CommentCreateRequest;
import com.redhorse.deokhugam.domain.comment.dto.CommentUpdateRequest;
import com.redhorse.deokhugam.domain.comment.entity.Comment;
import com.redhorse.deokhugam.domain.comment.repository.CommentRepository;
import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.domain.review.repository.ReviewRepository;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class CommentIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ReviewRepository reviewRepository;

  @Autowired
  private EntityManager em;

  @MockitoBean
  private AlarmService alarmService;

  User savedUser;
  Book savedBook;
  Review savedReview;
  UUID requestUserId;

  @BeforeEach
  void setUp() {
    savedUser = new User("Buzz@codeit.com", "버즈", "Thisistest123***");
    userRepository.save(savedUser);

    savedBook = new Book("스프링 부트 핵심", "김스프링", "스프링 부트 마스터하기", "IT북스",
        LocalDate.of(2024, 1, 1), "9788900000001", "url1", false, 4.8, 10L, new ArrayList<>());
    bookRepository.save(savedBook);

    savedReview = new Review("너무 재밌어요", 5, savedBook, savedUser);
    reviewRepository.save(savedReview);

    requestUserId = savedUser.getId();
  }

  @Nested
  @DisplayName("댓글 등록 관련 테스트")
  class createCommentsTests {

    @Test
    @DisplayName("댓글 등록 요청이 성공적으로 처리되어야 한다.")
    void create_Success() throws Exception {
      // given
      CommentCreateRequest request = new CommentCreateRequest(savedReview.getId(),
          savedUser.getId(), "댓글 생성 테스트");

      // when & then
      mockMvc.perform(post("/api/comments")
              .header("Deokhugam-Request-User-ID", requestUserId.toString())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.content").value("댓글 생성 테스트"))
          .andExpect(jsonPath("$.userNickname").value("버즈"));
    }

    @Test
    @DisplayName("댓글 등록 성공 - 알림 서비스에서 예외가 발생해도 댓글 생성은 유지되어야 한다.")
    void create_WhenAlarmServiceFails_ShouldStillReturnCreated() throws Exception {
      // given
      CommentCreateRequest request = new CommentCreateRequest(savedReview.getId(),
          savedUser.getId(), "알람 실패 테스트");

      doThrow(new RuntimeException("알람 서버 장애"))
          .when(alarmService).createCommentAlarm(any());

      // when & then
      mockMvc.perform(post("/api/comments")
              .header("Deokhugam-Request-User-ID", requestUserId.toString())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.content").value("알람 실패 테스트"));
    }

    @Test
    @DisplayName("댓글 등록 실패 - 존재하지 않는 리뷰 ID인 경우 404 Not Found를 반환한다.")
    void create_WhenReviewNotFound_ShouldReturnNotFound() throws Exception {
      // given
      UUID invalidReviewId = UUID.randomUUID();
      CommentCreateRequest request = new CommentCreateRequest(invalidReviewId, savedUser.getId(),
          "댓글 등록 실패 테스트");

      // when & then
      mockMvc.perform(post("/api/comments")
              .header("Deokhugam-Request-User-ID", requestUserId.toString())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("댓글 등록 실패 - 존재하지 않는 사용자 ID인 경우 404 Not Found를 반환한다.")
    void create_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
      // given
      UUID invalidUserID = UUID.randomUUID();
      CommentCreateRequest request = new CommentCreateRequest(savedReview.getId(), invalidUserID,
          "댓글 등록 실패 테스트");

      // when & then
      mockMvc.perform(post("/api/comments")
              .header("Deokhugam-Request-User-ID", requestUserId.toString())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("입력값 검증 실패 - valid 검증을 실패하면 400 Bad Request를 반환한다.")
    void create_InvalidInput_ShouldReturnBadRequest() throws Exception {
      // given
      CommentCreateRequest invalidRequest = new CommentCreateRequest(null, null, "");

      // when & then
      mockMvc.perform(post("/api/comments")
              .header("Deokhugam-Request-User-ID", requestUserId.toString())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(invalidRequest)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("댓글 수정 관련 테스트")
  class updateCommentsTests {

    @Test
    @DisplayName("댓글 수정 요청이 성공적으로 처리되어야 한다.")
    void update_Success() throws Exception {
      // given
      UUID requestUserId = savedUser.getId();
      Comment savedComment = new Comment("댓글 등록", savedReview, savedUser);

      commentRepository.save(savedComment);
      CommentUpdateRequest request = new CommentUpdateRequest("댓글 수정 테스트");

      // when & then
      mockMvc.perform(patch("/api/comments/{commentId}", savedComment.getId())
              .header("Deokhugam-Request-User-ID", requestUserId.toString())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(savedComment.getId().toString()))
          .andExpect(jsonPath("$.content").value("댓글 수정 테스트"))
          .andExpect(jsonPath("$.userId").value(requestUserId.toString()));
    }

    @Test
    @DisplayName("댓글 수정 실패 - 작성자가 아닌 유저가 요청하면 403 Forbidden을 반환한다")
    void update_WhenUserIsNotAuthor_ShouldReturnForbidden() throws Exception {
      // given
      User anotherUser = new User("other@codeit.com", "다른유저", "Password123***");
      userRepository.save(anotherUser);

      Comment savedComment = new Comment("댓글 등록", savedReview, savedUser);
      commentRepository.save(savedComment);

      CommentUpdateRequest request = new CommentUpdateRequest("댓글 수정 테스트");

      // when & then
      mockMvc.perform(patch("/api/comments/{commentId}", savedComment.getId())
              .header("Deokhugam-Request-User-ID", anotherUser.getId().toString())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("댓글 단건 조회 관련 테스트")
  class findCommentsTests {

    @Test
    @DisplayName("댓글 단건 조회 요청이 성공적으로 처리되어야 한다.")
    void find_Success() throws Exception {
      // given
      Comment savedComment = new Comment("댓글 등록", savedReview, savedUser);
      commentRepository.save(savedComment);

      // when & then
      mockMvc.perform(get("/api/comments/{commentId}", savedComment.getId())
              .header("Deokhugam-Request-User-ID", requestUserId.toString())
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(savedComment.getId().toString()))
          .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("댓글 단건 조회 실패 - 존재하지 않은 댓글 Id인 경우 404 Not Found를 반환한다.")
    void find_WhenCommentNotFound_ShouldReturnNotFound() throws Exception {
      // given
      UUID invalidCommentId = UUID.randomUUID();

      // when & then
      mockMvc.perform(get("/api/comments/{commentId}", invalidCommentId)
              .header("Deokhugam-Request-User-ID", requestUserId.toString())
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("댓글 논리 삭제 관련 테스트")
  class softDeleteCommentTests {

    @Test
    @DisplayName("댓글 논리 삭제 요청이 성공적으로 처리되어야 한다.")
    void softDelete_Success() throws Exception {
      // given
      UUID requestUserId = savedUser.getId();

      Comment savedComment = new Comment("댓글 등록", savedReview, savedUser);
      commentRepository.save(savedComment);

      // when
      mockMvc.perform(delete("/api/comments/{commentId}", savedComment.getId())
              .header("Deokhugam-Request-User-ID", requestUserId.toString()))
          .andExpect(status().isNoContent());

      // then
      Comment deletedComment = commentRepository.findById(savedComment.getId()).orElseThrow();
      assertThat(deletedComment.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("댓글 논리 삭제 실패 - 존재하지 않은 댓글 Id인 경우 404 Not Found를 반환한다.")
    void softDelete_WhenCommentNotFound_ShouldReturnNotFound() throws Exception {
      // given
      UUID invalidCommentId = UUID.randomUUID();
      UUID requestUserId = savedUser.getId();

      // when & then
      mockMvc.perform(delete("/api/comments/{commentId}", invalidCommentId)
              .header("Deokhugam-Request-User-ID", requestUserId.toString()))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("댓글 논리 삭제 실패 - 작성자가 아닌 유저가 요청하면 403 Forbidden을 반환한다.")
    void softDelete_WhenUserIsNotAuthor_ShouldReturnForbidden() throws Exception {
      // given
      User anotherUser = new User("other@codeit.com", "다른유저", "Password123***");
      userRepository.save(anotherUser);

      Comment savedComment = new Comment("댓글 등록", savedReview, savedUser);
      commentRepository.save(savedComment);

      // when & then
      mockMvc.perform(delete("/api/comments/{commentId}", savedComment.getId())
              .header("Deokhugam-Request-User-ID", anotherUser.getId().toString()))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("댓글 논리 삭제 실패 - 요청 헤더가 누락된 경우 401 Unauthorized를 반환한다")
    void softDelete_WhenHeaderMissing_ShouldReturnUnauthorized() throws Exception {
      // given
      Comment savedComment = new Comment("댓글 등록", savedReview, savedUser);
      commentRepository.save(savedComment);

      // when & then
      mockMvc.perform(delete("/api/comments/{commentId}", savedComment.getId()))
          .andExpect(status().isUnauthorized());
    }
  }

  @Nested
  @DisplayName("댓글 물리 삭제 관련 테스트")
  class hardDeleteCommentTests {

    @Test
    @DisplayName("댓글 물리 삭제 요청이 성공적으로 처리되어야 한다.")
    void hardDelete_Success() throws Exception {
      // given
      UUID requestUserId = savedUser.getId();

      Comment savedComment = new Comment("댓글 등록", savedReview, savedUser);
      commentRepository.save(savedComment);

      // when & then
      mockMvc.perform(delete("/api/comments/{commentId}/hard", savedComment.getId())
              .header("Deokhugam-Request-User-ID", requestUserId.toString()))
          .andExpect(status().isNoContent());

      boolean exists = commentRepository.existsById(savedComment.getId());
      assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("댓글 물리 삭제 실패 - 존재하지 않은 댓글 Id인 경우 404 Not Found를 반환한다.")
    void hardDelete_WhenCommentNotFound_ShouldReturnNotFound() throws Exception {
      // given
      UUID invalidCommentId = UUID.randomUUID();
      UUID requestUserId = savedUser.getId();

      // when & then
      mockMvc.perform(delete("/api/comments/{commentId}/hard", invalidCommentId)
              .header("Deokhugam-Request-User-ID", requestUserId.toString()))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("댓글 물리 삭제 실패 - 작성자가 아닌 유저가 요청하면 403 Forbidden을 반환한다.")
    void hardDelete_WhenUserIsNotAuthor_ShouldReturnForbidden() throws Exception {
      // given
      User anotherUser = new User("other@codeit.com", "다른유저", "Password123***");
      userRepository.save(anotherUser);

      Comment savedComment = new Comment("댓글 등록", savedReview, savedUser);
      commentRepository.save(savedComment);

      // when & then
      mockMvc.perform(delete("/api/comments/{commentId}/hard", savedComment.getId())
              .header("Deokhugam-Request-User-ID", anotherUser.getId().toString()))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("댓글 물리 삭제 실패 - 요청 헤더가 누락된 경우 401 Unauthorized를 반환한다")
    void hardDelete_WhenHeaderMissing_ShouldReturnUnauthorized() throws Exception {
      // given
      Comment savedComment = new Comment("댓글 등록", savedReview, savedUser);
      commentRepository.save(savedComment);

      // when & then
      mockMvc.perform(delete("/api/comments/{commentId}/hard", savedComment.getId()))
          .andExpect(status().isUnauthorized());
    }
  }

  @Nested
  @DisplayName("댓글 목록 조회 관련 테스트")
  class findAllCommentTests {

    @Test
    @DisplayName("댓글 목록 조회 요청이 성공적으로 처리되어야 한다.")
    void findAll_Success() throws Exception {
      // given
      commentRepository.save(new Comment("댓글 1", savedReview, savedUser));
      commentRepository.save(new Comment("댓글 2", savedReview, savedUser));
      commentRepository.save(new Comment("댓글 3", savedReview, savedUser));

      commentRepository.flush();
      em.clear();

      // when & then
      mockMvc.perform(get("/api/comments")
              .header("Deokhugam-Request-User-ID", requestUserId.toString())
              .param("reviewId", savedReview.getId().toString())
              .param("direction", "DESC")
              .param("limit", "5"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content.length()").value(3))
          .andExpect(jsonPath("$.size").value(5))
          .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    @DisplayName("댓글 목록 조회 시 삭제된 댓글은 조회되지 않아야한다.")
    void findAll_ExcludeDelete_Success() throws Exception {
      // given
      Comment comment1 = commentRepository.save(new Comment("댓글 1", savedReview, savedUser));
      Comment comment2 = commentRepository.save(new Comment("댓글 2", savedReview, savedUser));
      Comment comment3 = commentRepository.save(new Comment("댓글 3", savedReview, savedUser));

      comment2.softDelete();

      commentRepository.flush();
      em.clear();

      // when & then
      mockMvc.perform(get("/api/comments")
              .header("Deokhugam-Request-User-ID", requestUserId.toString())
              .param("reviewId", savedReview.getId().toString())
              .param("direction", "DESC")
              .param("limit", "5"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content.length()").value(2))
          .andExpect(jsonPath("$.content[0].content").value("댓글 3"))
          .andExpect(jsonPath("$.content[1].content").value("댓글 1"));
    }

    @Test
    @DisplayName("댓글 목록 조회 실패 - reviewId가 누락되면 400 BadRequest를 반환한다")
    void findAll_WhenInvalidRequest_ShouldReturnBadRequest() throws Exception {
      // given & when & then
      // review Id 파라미터 누락
      mockMvc.perform(get("/api/comments")
              .header("Deokhugam-Request-User-ID", requestUserId.toString())
              .param("direction", "DESC")
              .param("limit", "5"))
          .andExpect(status().isBadRequest());
    }
  }
}
