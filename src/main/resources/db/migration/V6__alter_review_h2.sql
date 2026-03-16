
ALTER TABLE reviews DROP CONSTRAINT IF EXISTS uk_book_user_id;

CREATE UNIQUE INDEX IF NOT EXISTS review_unique_active_user_book ON reviews (book_id, user_id);