# 팀원 안내 — 최신 develop 반영 시 필요한 작업

`Feat flyway v3 … 인증 인가 적용` 머지 이후, **각자 로컬에서 아래만 하면 됩니다.** 대부분 자동입니다.

## TL;DR (정상 케이스)

```bash
git pull                 # V3 마이그레이션 + 설정 변경 받기
docker compose up -d     # Redis 없으면 이걸로 띄움 (localhost:6379)
# 앱 재시작 → Flyway 가 V3 를 자동 적용 (users.user_name 컬럼 제거)
```

DB를 손으로 건드린 적 없다면 **수동 작업 0**. 재시작하면 Flyway 가 알아서 V3 까지 올립니다.

---

## 1. Flyway V3 — 회원가입 500 수정 (자동 적용)

- 증상: `POST /api/v1/auth/register` → 500 `Field 'user_name' doesn't have a default value`
- 원인: V1 스키마의 `users.user_name` 이 NOT NULL 인데 엔티티에 매핑이 없어(중복 잉여 컬럼) INSERT 실패.
- 조치: `V3__drop_users_user_name.sql` 이 해당 컬럼을 DROP. **DB를 직접 고칠 필요 없음** — 앱 재시작 시 Flyway 가 version 2 → 3 으로 자동 마이그레이션.

### ⚠️ 예외 — 이미 DB를 손으로 고친 사람만 해당

register 를 되게 하려고 로컬에서 `user_name` 을 직접 지웠거나 nullable/default 로 바꿨다면, V3 의 `DROP COLUMN user_name` 이 실패해 앱이 안 뜹니다. 이 경우만:

- 로컬 데이터가 아깝지 않으면 **스키마 초기화가 제일 깔끔**:
  ```sql
  DROP DATABASE mac_db; CREATE DATABASE mac_db
    DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  ```
  재시작하면 V1→V2→V3 처음부터 정상 적용.
- 데이터를 지키고 싶으면 `user_name` 을 다시 만들어 V3 가 지울 수 있게 하거나, `flyway_schema_history` 를 손봐야 함 → 그 전에 공유 채널에 물어보세요.

> 마이그레이션은 **DB로 전달하는 게 아니라 git 의 SQL 파일로 전달**됩니다. 각자 앱이 뜰 때 자기 DB에 적용합니다. 그래서 별도 "DB 전달" 과정은 없습니다.

---

## 2. Redis 필수 (없으면 인증 전부 실패)

로그인/로그아웃/재발급뿐 아니라 **토큰이 실린 모든 요청**이 블랙리스트 검사로 Redis 를 호출합니다. Redis 가 없으면 로그인 실패 + 인증 요청 500.

- 이미 6379 에 Redis 가 있으면 그대로 사용.
- 없으면 레포 루트의 `docker-compose.yml` 로 띄우세요:
  ```bash
  docker compose up -d      # projectmac-redis, localhost:6379, 비번 없음
  docker compose ps
  ```
- 앱 설정(`application.yml`)은 기본 `localhost:6379` / 비번 없음. 환경변수로 덮을 수 있음:
  `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`.

---

## 3. 그 밖의 변경 (참고)

- `GET /api/v1/health` → **공개(permitAll)** 로 변경. 토큰 없이 헬스체크 가능.
- `POST /api/v1/data-sources` → 하드코딩 STUB 유저 제거, **로그인 유저(Bearer 토큰)** 로 소유자 기록. 호출 시 `Authorization: Bearer <accessToken>` 필요.

---

## 로컬 실행 체크리스트

1. MySQL `mac_db` 기동 (계정: `mac_user` / `mac1234`, 포트 3306)
2. Redis 6379 기동 (`docker compose up -d`)
3. 앱 실행 (프로파일 `local`) → 로그에 `Current version of schema mac_db: 3` 확인
4. Swagger 에서 `register → login` 순으로 동작 확인
