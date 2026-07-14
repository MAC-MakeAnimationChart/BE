-- users.user_name 은 name 과 중복인 잉여 컬럼이며 어떤 엔티티에도 매핑되지 않는다.
-- NOT NULL + default 없음이라 회원가입 INSERT(name 만 채움)가 실패하므로 제거한다.
ALTER TABLE users DROP COLUMN user_name;
