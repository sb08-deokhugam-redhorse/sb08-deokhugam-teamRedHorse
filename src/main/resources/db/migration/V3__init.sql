-- 기존 pupular_books 내용을 비움
TRUNCATE TABLE popular_books;

-- score 컬럼 타입을 BIGINT에서 DOUBLE PRECISION으로 변경
ALTER TABLE popular_books ALTER COLUMN score TYPE DOUBLE PRECISION;