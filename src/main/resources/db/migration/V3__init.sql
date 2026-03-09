-- 컬럼명 변경 (thumbnail_url -> thumbnail_key)
ALTER TABLE books RENAME COLUMN thumbnail_url TO thumbnail_key;

-- 컬럼 데이터 타입 및 사이즈 변경 (VARCHAR(255) -> VARCHAR(100))
ALTER TABLE books ALTER COLUMN thumbnail_key TYPE VARCHAR(100);

-- score 컬럼 타입을 BIGINT에서 DOUBLE PRECISION으로 변경
ALTER TABLE popular_books ALTER COLUMN score TYPE DOUBLE PRECISION;