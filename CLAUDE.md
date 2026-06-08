# MAC Project — CLAUDE.md

## 프로젝트 개요

**프로젝트명:** MAC (My Analytics Chart)
**스택:** Java 17 · Spring Boot 3.5 · Spring Security · Spring Data JPA · MySQL · Lombok
**패키지 루트:** `com.mac.projectmac`
**빌드 도구:** Gradle

차트 분석 플랫폼. 유저가 데이터 소스를 업로드하고 차트를 생성·공유하는 서비스.

---

## 담당 도메인: Logging & Monitoring (고성민)

이 저장소에서 Claude가 작업하는 영역은 **`global/logging`** 패키지 하위이다.

### 내 담당 API (모두 개발 전)

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/api/v1/admin/logs` | 에러/경고 로그 목록 조회 |
| GET | `/api/v1/admin/logs/{logId}` | 로그 단건 조회 |
| GET | `/api/v1/admin/logs/slow-queries` | 슬로우 쿼리 목록 조회 |
| PUT | `/api/v1/admin/notifications/settings` | Discord/Slack Webhook 설정 수정 |
| POST | `/api/v1/admin/notifications/test` | 테스트 알림 발송 |
| GET | `/api/v1/health` | 서버 및 DB 상태 확인 |

### 협업 경계

- **`GlobalExceptionHandler`** (`global/api/common`) — 이강욱 소유. 직접 수정 금지.
  - 5xx 예외 시 `AlertService`를 주입받아 알림 트리거하는 방식으로 연동.
- **`@Async` 사용 시** MDC 전달 처리 필요 (부모 스레드 MDC 자동 상속 안 됨).
- **P6Spy** — dev/staging 환경에서만 활성화, prod는 일반 MySQL Driver 사용.

---

## 패키지 구조

```
com.mac.projectmac
├── global
│   ├── api/common          # ApiResponse, ApiErrorResponse, GlobalExceptionHandler (이강욱)
│   ├── domain/common       # DomainException, ErrorCode 인터페이스, 예외 클래스들
│   └── logging             # 고성민 담당 — AOP, MDC Filter, P6Spy, Alert, Admin API
├── admin                   # admin 도메인 (readme.md 존재)
├── auth                    # 인증 도메인 (readme.md 존재)
└── project                 # 프로젝트 도메인
```

### Logging 패키지 내부 계획 구조

```
global/logging
├── aop                     # RequestLoggingAspect, ExecutionTimeAspect
├── filter                  # MdcLoggingFilter (requestId, userId MDC 등록)
├── p6spy                   # CustomP6SpyFormatter, SlowQueryLogger
├── alert
│   ├── AlertSender.java    # 인터페이스
│   ├── DiscordAlertSender.java
│   ├── SlackAlertSender.java
│   └── AlertService.java   # @Async, Throttling 포함
├── admin
│   ├── controller          # LogAdminController, NotificationController, HealthController
│   ├── service
│   ├── repository
│   └── dto
└── config                  # AsyncConfig (알림 전용 스레드풀), LoggingProperties
```

---

## 응답 형식

모든 API는 기존 `ApiResponse<T>` 사용.

```java
// 성공
ApiResponse.success(code, message, data)   // 200
ApiResponse.created(code, message, data)   // 201

// 에러 (GlobalExceptionHandler가 처리)
ApiErrorResponse.of(httpStatus, errorCode, uri)
```

응답 필드: `{ timestamp, status, code, message, data }`

---

## 에러코드 (Logging 도메인)

| code | HTTP | 설명 |
|------|------|------|
| `LOG_NOT_FOUND` | 404 | 해당 로그를 찾을 수 없음 |
| `LOG_ACCESS_FORBIDDEN` | 403 | 관리자 권한 없는 로그 접근 |
| `NOTIFICATION_INVALID_URL` | 400 | Webhook URL 형식 오류 |
| `NOTIFICATION_INVALID_CHANNEL` | 400 | channel 값이 DISCORD/SLACK/ALL 이외 |
| `NOTIFICATION_WEBHOOK_NOT_SET` | 400 | 해당 채널 Webhook URL 미설정 |
| `NOTIFICATION_SEND_FAILED` | 502 | 외부 Webhook 발송 실패 |
| `HEALTH_DB_DOWN` | 503 | DB 연결 실패 |

에러코드는 `global/domain/common/error/ErrorCode` 인터페이스를 구현하는 enum으로 선언.

---

## DB 스키마 (참고)

| 테이블 | 주요 컬럼 |
|--------|-----------|
| `users` | user_id, login_id, email, role(ENUM), status(ENUM), is_deleted |
| `refresh_tokens` | token_id, token_value, ip_address, user_agent, expired_at |
| `project` | project_id, user_id, project_name, thumbnail, folder_id |
| `chart_option` | chart_option_id, chart_type, data_mapping(JSON), style_option(JSON) |
| `folders` | id, user_id, parent_id, name |
| `data_sources` | SourceID, source_type(ENUM: UPLOAD/CLIPBOARD/SAMPLE), status(ENUM: PENDING/PARSING/COMPLETED/FAILED) |

로그 저장 테이블은 신규 생성 필요 (`app_logs`, `slow_query_logs`).

---

## 로깅 컨벤션

### Logger 선언

```java
@Slf4j  // 항상 Lombok 사용
@Service
public class AlertService { }
```

### 메시지 포맷

```
[클래스명] 행위 - 핵심 식별자=값
```

```java
log.info("[AlertService] 알림 발송 완료 - channel={}, exceptionClass={}", channel, exClass);
log.warn("[SlowQueryLogger] 슬로우 쿼리 감지 - executionMs={}", ms);
log.error("[AlertService] Webhook 발송 실패 - channel={}", channel, e);
```

### 레벨 기준

| 레벨 | 기준 |
|------|------|
| DEBUG | 개발 중 흐름 확인, 운영 미출력 |
| INFO | 정상 비즈니스 흐름 |
| WARN | 4xx, 슬로우쿼리, 재시도 |
| ERROR | 5xx, 즉시 조치 필요 |

### 절대 금지

- `System.out.println` — Logger 사용
- 문자열 연결(`+`) — `{}` 파라미터 바인딩
- 민감정보 출력 — password, token, cardNumber, Authorization
- 예외 로그에서 예외 객체 누락
- Domain(Aggregate) 내부에 log 코드 작성
- catch에서 예외 삼키기 — 반드시 `throw`

### 레이어별 규칙

- **Controller** — 직접 로그 불필요 (AOP 자동 처리)
- **Service** — 핵심 비즈니스 흐름 위주
- **Domain(Aggregate)** — 로그 금지, Domain Event로 대체
- **Repository** — 직접 로그 불필요 (P6Spy 자동 처리)

---

## 핵심 구현 규칙

### MDC

- `MdcLoggingFilter`에서 `requestId`(UUID), `userId` 등록 → `finally`에서 `MDC.clear()`
- `@Async` 사용 시 반드시 `MDC.getCopyOfContextMap()`으로 MDC 복사 후 전달

### Alert 비동기 처리

```java
// 알림 전용 스레드풀
corePoolSize=2, maxPoolSize=5, queueCapacity=100
```

- `AlertSender` 인터페이스 → `DiscordAlertSender` / `SlackAlertSender` 구현체 분리
- Throttling: 동일 예외 클래스 1분에 1회만 발송
- Webhook 실패 시 비즈니스 로직에 영향 없도록 `try-catch` 격리

### P6Spy

- 완성형 SQL 출력 (파라미터 바인딩 포함)
- 슬로우 쿼리 기준: 1000ms (application.yml 관리)
- commit/rollback 로그 제외
- 환경 분리: dev → P6SpyDriver, prod → MySQL Driver

### AOP

- 바디 최대 2000자, 초과 시 `[truncated...]`
- 제외 URL: `/api/v1/health`, `/actuator/**`, `/favicon.ico` (application.yml 관리)
- `multipart/form-data` 요청은 메타정보만 기록
- Controller threshold: 500ms WARN, Service threshold: 1000ms WARN

### 설정값 외부 관리

Webhook URL, threshold, 마스킹 대상 필드 목록 — 코드 하드코딩 금지, `application.yml` + 환경변수로 관리.

---

## 브랜치 전략

```
develop          # 통합 브랜치 (PR 타겟)
└── feature/logging-{기능명}    # 기능 단위 브랜치
```

### 브랜치 네이밍 예시

```
feature/logging-mdc-filter
feature/logging-request-aop
feature/logging-p6spy
feature/logging-alert-service
feature/logging-admin-api
feature/logging-health-api
```

### 이슈/PR 규칙

- 이슈 먼저 생성 → 브랜치 파서 → 개발 → PR (`develop` 타겟)
- PR 제목: `[feat] 기능명` / `[fix] 수정내용`
- PR에 관련 이슈 번호 링크

---

## 자주 쓰는 명령

```bash
# 빌드
./gradlew build

# 테스트
./gradlew test

# 로컬 실행 (local 프로파일)
./gradlew bootRun --args='--spring.profiles.active=local'
```
