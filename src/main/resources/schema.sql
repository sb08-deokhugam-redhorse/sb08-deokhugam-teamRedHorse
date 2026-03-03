DROP TABLE IF EXISTS popular_review CASCADE;
DROP TABLE IF EXISTS popular_books CASCADE;
DROP TABLE IF EXISTS power_users CASCADE;
DROP TABLE IF EXISTS alarm CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS review_like CASCADE;
DROP TABLE IF EXISTS review CASCADE;
DROP TABLE IF EXISTS books CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id         UUID PRIMARY KEY,
    email      VARCHAR(255)             NOT NULL UNIQUE,
    nickname   VARCHAR(20)              NOT NULL,
    password   VARCHAR(255)             NOT NULL,
    is_deleted BOOLEAN                  NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS books
(
    id             UUID PRIMARY KEY,
    title          VARCHAR(100)                  NOT NULL,
    author         VARCHAR(50)                   NOT NULL,
    description    TEXT                          NOT NULL,
    publisher      VARCHAR(50)                   NOT NULL,
    published_date DATE                          NOT NULL,
    thumbnail_url  VARCHAR(255),
    isbn           VARCHAR(13)                   NOT NULL UNIQUE,
    is_deleted     BOOLEAN                       NOT NULL DEFAULT FALSE,
    rating         DOUBLE PRECISION              NOT NULL CHECK (rating BETWEEN 0 AND 5),
    review_count   BIGINT                        NOT NULL DEFAULT 0,
    created_at     TIMESTAMP WITH TIME ZONE      NOT NULL,
    updated_at     TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS reviews
(
    id         uuid        PRIMARY KEY,
    rating     int         NOT NULL,
    content    text        NOT NULL,
    likes      bigint      NOT NULL DEFAULT 0,
    comments   bigint      NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL ,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL ,
    deleted_at TIMESTAMP WITH TIME ZONE,
    book_id    uuid        NOT NULL,
    user_id    uuid        NOT NULL,

    CONSTRAINT fk_book_id FOREIGN KEY (book_id) REFERENCES books (id),
    CONSTRAINT fk_reviews_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uk_book_user_id UNIQUE (book_id, user_id),
    CONSTRAINT check_rating CHECK ( rating BETWEEN 1 AND 5)
);

Create TABLE IF NOT EXISTS review_likes
(
    id         uuid                     PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE,
    user_id    uuid        NOT NULL,
    review_id  uuid        NOT NULL,

    CONSTRAINT fk_reviews_likes_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_review_id FOREIGN KEY (review_id) REFERENCES reviews (id),
    CONSTRAINT uk_user_review_like_id UNIQUE (user_id, review_id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id         uuid PRIMARY KEY,
    content    text                                               NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE,
    review_id  uuid                                               NOT NULL,
    user_id    uuid                                               NOT NULL,

    CONSTRAINT fk_comments_review
    FOREIGN KEY (review_id)
    REFERENCES reviews (id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_user
    FOREIGN KEY (user_id)
    REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS alarms
(
    id         UUID         NOT NULL PRIMARY KEY,
    type       VARCHAR(10)  NOT NULL,
    contents   VARCHAR(255) NOT NULL,
    recipient  UUID         NOT NULL,
    sender     VARCHAR(100) NOT NULL,
    link       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_alarms_user FOREIGN KEY (recipient) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS power_users
(
    id               UUID                         NOT NULL PRIMARY KEY,
    period           VARCHAR(20)                  NOT NULL,
    ranking          BIGINT                       NOT NULL,
    score            DOUBLE PRECISION DEFAULT 0.0 NOT NULL,
    review_score_sum DOUBLE PRECISION DEFAULT 0.0 NOT NULL,
    like_count       INTEGER          DEFAULT 0   NOT NULL,
    comment_count    INTEGER          DEFAULT 0   NOT NULL,
    created_at       TIMESTAMP WITH TIME ZONE     NOT NULL,
    user_id          UUID                         NOT NULL,

    CONSTRAINT fk_power_users_users FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS popular_books
(
    id           UUID                         NOT NULL PRIMARY KEY,
    book_id      UUID                         NOT NULL,
    period       VARCHAR(20)                  NOT NULL,
    rating       DOUBLE PRECISION DEFAULT 0.0 NOT NULL,
    score        BIGINT           DEFAULT 0   NOT NULL,
    review_count BIGINT           DEFAULT 0   NOT NULL,
    ranking      BIGINT           DEFAULT 0   NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE     NOT NULL,

    CONSTRAINT fk_popular_books_books FOREIGN KEY (book_id) REFERENCES books (id)
);

CREATE TABLE IF NOT EXISTS popular_reviews
(
    id            UUID                         NOT NULL PRIMARY KEY,
    period        VARCHAR(20)                  NOT NULL,
    ranking       BIGINT                       NOT NULL,
    score         DOUBLE PRECISION DEFAULT 0.0 NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE     NOT NULL,
    like_count    BIGINT           DEFAULT 0   NOT NULL,
    comment_count BIGINT           DEFAULT 0   NOT NULL,
    review_id     UUID                         NOT NULL,

    CONSTRAINT fk_popular_review_review FOREIGN KEY (review_id) REFERENCES reviews (id)
);
