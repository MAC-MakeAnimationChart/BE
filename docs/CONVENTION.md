# API 응답 / 예외 처리 / 에러코드 컨벤션

MAC(Make Animation Chart) 백엔드의 글로벌 공통 구조(응답·예외·에러코드·Swagger)에 대한 팀 컨벤션 문서입니다.

> 결정·이유는 [`docs/adr/0004-error-response-standard.md`](adr/0004-error-response-standard.md)(스트리밍 예외는 [0005](adr/0005-streaming-response-standard.md)).
> 분할 문서: 에러/예외 [`convention/exception.md`](convention/exception.md), 성공 응답 [`convention/response.md`](convention/response.md), 코드 작성 [`convention/code.md`](convention/code.md).

---

## 1. 에러코드 네이밍

### 포맷
```
{DOMAIN}-{NNN}          // 단일 도메인 (시퀀스)
```

### 코드표 (현 코드 기준)

| 도메인 | 접두어 | 실제 코드 |
|--------|--------|----------|
| `chart.option` | `CHO` | `CHO-001`(옵션 없음), `CHO-002`(미지원 차트 타입), `CHO-003`(잘못된 dataMapping), `CHO-004`(잘못된 styleOption), `CHO-409`(이미 존재) |
| `project` | `PRJ` | `PRJ-001`(프로젝트 없음) — 현재 `chart.option`의 `ChartOptionErrorCode`에 정의됨 |
| `auth`(공통 인증) | `AUT` | `AUT-015`(403 권한 없음), `AUT-016`(401 인증 필요) — `GlobalExceptionHandler`가 발급 |

> ⚠️ 두 가지 예외(현 코드 기준, 추후 정리 대상):
> - `datasource`는 아직 `{DOMAIN}-{NNN}`이 아니라 **이름 그대로** 코드를 쓴다: `INVALID_REQUEST`, `UNSUPPORTED_SOURCE_TYPE`, `UPLOAD_FILE_REQUIRED`, `FILE_STORAGE_FAILED`.
> - `project.ProjectErrorCode`는 프로젝트가 아니라 **채팅방 코드(`CHT-001~003`)**를 담고 있다(타 출처 잔재).

### 공통 코드 (`GlobalExceptionHandler`)

| 코드 | HTTP | 용도 |
|------|------|------|
| `COMMON-VALIDATION-FAILED` | 400 | `@Valid` 검증 실패 (필드별 `errors` 포함) |
| `COMMON-BAD-REQUEST` | 400 | 파라미터 누락·타입 불일치·본문 파싱 실패 |
| `FILE_SIZE_EXCEEDED` | 413 | 업로드 용량 초과 |
| `INTERNAL_ERROR` | 500 | 처리되지 않은 예외 |

### 에러코드 추가 방법
1. 해당 도메인의 `XxxErrorCode` enum(`implements ErrorCode`)에 상수 추가
2. 코드 문자열은 위 코드표 prefix + 시퀀스 번호
3. **개별 Exception 클래스 신규 생성 금지** — 공통 예외 타입 사용

```java
// Good
throw new NotFoundException(ChartOptionErrorCode.CHART_OPTION_NOT_FOUND);

// Bad — 개별 클래스 만들지 말 것
throw new ChartOptionNotFoundException();
```

---

## 2. 예외 처리 구조

### 공통 예외 타입 (`global.domain.common.error.exception`)

모두 추상 클래스 `global.domain.common.DomainException`(`ErrorCode` 보유 + `getHttpStatus()`)을 상속한다.

| 클래스 | HTTP Status | 용도 |
|--------|-------------|------|
| `NotFoundException` | 404 | 리소스 없음 |
| `ValidationException` | 400 | 입력값 검증 실패 |
| `UnauthorizedException` | 401 | 인증 실패 |
| `ForbiddenException` | 403 | 권한 없음 |
| `ConflictException` | 409 | 중복 / 충돌 |
| `TooManyRequestsException` | 429 | 요청 한도 초과 |
| `ExternalServiceException` | 502 | 외부 서비스 연동 실패 |

> 현 코드 기준 예외: 글로벌에 422(Unprocessable Entity) 공통 타입이 없어 `chart.option`은 도메인 전용 `ChartOptionUnprocessableEntityException`(422)을 두고 `CHO-002/003/004`에 쓴다.

### 사용 예시

```java
// 조회 실패
chartOptionRepository.findByProjectId(projectId)
    .orElseThrow(() -> new NotFoundException(ChartOptionErrorCode.CHART_OPTION_NOT_FOUND));

// 중복
if (exists) throw new ConflictException(ChartOptionErrorCode.CHART_OPTION_ALREADY_EXISTS);

// 외부 연동 실패 (파일 저장)
throw new ExternalServiceException(DataSourceErrorCode.FILE_STORAGE_FAILED);
```

### 에러 응답 포맷 (`ApiErrorResponse`, `global.api.common`)

```json
{
  "timestamp": "2026-05-21T10:00:00Z",
  "status": 404,
  "code": "CHO-001",
  "message": "차트 옵션을 찾을 수 없습니다.",
  "path": "/api/v1/projects/1/chart-option",
  "errors": []
}
```

> `errors`는 `@Valid` 검증 실패(`COMMON-VALIDATION-FAILED`)일 때만 필드별로 채워지고, 그 외에는 빈 배열이다.

---

## 3. 성공 응답 포맷 (`ApiResponse`, `global.api.common`)

정적 팩토리: `success(code, message, data)`(200) / `success(code, message)`(데이터 없는 200) / `created(code, message, data)`(201).

```java
// 조회 (200)
return ResponseEntity.ok(ApiResponse.success(
    ChartOptionResponseCode.OK,
    ChartOptionResponseMessage.OK,
    ChartOptionDetailResponse.from(getChartOptionUseCase.getByProjectId(projectId))
));

// 생성 (201)
return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(
    ChartOptionResponseCode.CREATED,
    ChartOptionResponseMessage.CREATED,
    ChartOptionResponse.from(registerChartOptionUseCase.register(request.toCommand(projectId)))
));
```

### ResponseCode / ResponseMessage 파일 위치 및 구조

각 도메인 `presentation/api/` 패키지에 `XxxResponseCode.java` + `XxxResponseMessage.java`를 함께 둔다.

```
chart/option/presentation/api/
├── ChartOptionController.java
├── ChartOptionResponseCode.java     ← 성공 코드 상수 (OK=CHO-200, SAVED=CHO-200, CREATED=CHO-201)
└── ChartOptionResponseMessage.java  ← 성공 메시지 상수
```

**작성 규칙:**
- 상수명은 행위/상태 결과로: `OK`, `SAVED`, `CREATED`
- 코드 문자열 포맷: `{접두어}-{HTTP상태}` (현 코드 기준 — 예: `CHO-200`, `CHO-201`)
- 생성자 `private` — 인스턴스화 금지

> ⚠️ divergence: `datasource`는 별도 상수 클래스 없이 컨트롤러에서 `"DATA-SOURCE-CREATED"` 리터럴을 직접 쓴다. 신규 작성 시 상수 클래스 방식으로 통일 권장.

### 도메인별 코드/메시지 파일 현황

| 도메인 | ResponseCode | ResponseMessage |
|--------|-------------|-----------------|
| chart.option | `ChartOptionResponseCode` | `ChartOptionResponseMessage` |
| datasource | (없음 — 컨트롤러 리터럴 `"DATA-SOURCE-CREATED"`) | — |

### 성공 응답 포맷

```json
{
  "timestamp": "2026-05-21T10:00:00Z",
  "status": 200,
  "code": "CHO-200",
  "message": "차트 옵션 조회 성공",
  "data": { ... }
}
```

---

## 4. Swagger 작성 가이드

의존성: `springdoc-openapi-starter-webmvc-ui:2.8.6`
Swagger UI: `http://localhost:8080/swagger-ui/index.html`

### 새 엔드포인트 작성 예시

```java
@Operation(summary = "프로젝트 차트 옵션 조회")
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "조회 성공"),
    @ApiResponse(responseCode = "404", description = "CHO-001: 차트 옵션을 찾을 수 없음")
})
@GetMapping
public ResponseEntity<ApiResponse<?>> getChartOption(@PathVariable Long projectId) { ... }
```

### 규칙
- 공통 에러(`401`, `403`, `500`)는 생략 가능 — `GlobalExceptionHandler`에서 자동 처리
- 도메인별 에러만 `@ApiResponse`로 명시
- `description`에 에러코드 포함: `"CHO-001: 차트 옵션을 찾을 수 없음"`
