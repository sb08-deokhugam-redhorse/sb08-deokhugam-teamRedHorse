package com.redhorse.deokhugam.reviewTest;

import com.redhorse.deokhugam.domain.book.entity.Book;
import com.redhorse.deokhugam.domain.book.repository.BookRepository;
import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.domain.review.repository.ReviewRepository;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;

import org.hibernate.exception.ConstraintViolationException;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
public class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Review createTestReview(String content, int rating, Book book, User user) {
        Review review = new Review(content, rating, book, user);
        return reviewRepository.save(review);
    }

    @Test
    @DisplayName("사용자는 책 하나당 1개의 리뷰만 입력할 수 있다.")
    void onlyOneBook_OneReview() {
        // given
       // 테스트를 위해 임시 생성자 사용
        Book book = new Book(
                "title",
                "author",
                "description",
                "publisher",
                LocalDate.now(),
                "isbn",
                false);
        bookRepository.save(book);
        User user = new User("email", "nickname", "password");
        userRepository.save(user);

        createTestReview("리뷰1", 3, book, user);

        entityManager.flush();
        entityManager.clear();

        // when & then
        assertThatThrownBy(() -> {
            createTestReview("리뷰2", 5, book, user);
            entityManager.flush();
        }).isInstanceOf(ConstraintViolationException.class);

    }
}
