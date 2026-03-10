-- 1. reviews 테이블 (유저 삭제 시 해당 유저의 리뷰 삭제)
ALTER TABLE reviews DROP CONSTRAINT IF EXISTS fk_reviews_user_id;
ALTER TABLE reviews ADD CONSTRAINT fk_reviews_user_id
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

-- 2. review_likes 테이블 (유저 삭제 시 해당 유저가 누른 좋아요 삭제)
ALTER TABLE review_likes DROP CONSTRAINT IF EXISTS fk_reviews_likes_user_id;
ALTER TABLE review_likes ADD CONSTRAINT fk_reviews_likes_user_id
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

-- 3. review_likes 테이블 (리뷰 삭제 시 해당 리뷰에 달린 좋아요 삭제)
ALTER TABLE review_likes DROP CONSTRAINT IF EXISTS fk_review_id;
ALTER TABLE review_likes ADD CONSTRAINT fk_review_id
    FOREIGN KEY (review_id) REFERENCES reviews (id) ON DELETE CASCADE;

-- 4. alarms 테이블 (유저 삭제 시 해당 유저의 알림 삭제)
ALTER TABLE alarms DROP CONSTRAINT IF EXISTS fk_alarms_users;
ALTER TABLE alarms ADD CONSTRAINT fk_alarms_users
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

-- 5. power_users 테이블 (유저 삭제 시 파워 유저 데이터 삭제)
ALTER TABLE power_users DROP CONSTRAINT IF EXISTS fk_power_users_users;
ALTER TABLE power_users ADD CONSTRAINT fk_power_users_users
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

-- 6. popular_reviews 테이블 (리뷰 삭제 시 인기 리뷰 데이터 삭제)
ALTER TABLE popular_reviews DROP CONSTRAINT IF EXISTS fk_popular_review_review;
ALTER TABLE popular_reviews ADD CONSTRAINT fk_popular_review_review
    FOREIGN KEY (review_id) REFERENCES reviews (id) ON DELETE CASCADE;
