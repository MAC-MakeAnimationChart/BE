# 예외 처리 / 에러코드 컨벤션

> 에러코드 네이밍 + 공통 예외 타입 + 에러 응답 포맷. 결정·이유는 [`docs/adr/0004-error-response-standard.md`](../adr/0004-error-response-standard.md).
> 관련: 성공 응답은 [`response.md`](response.md), 코드 작성 규칙(개별 예외클래스 금지 등)은 [`code.md`](code.md).

## 1. 에러코드 네이밍

### 포맷
```
{DOMAIN}-{NNN}              // 단일 도메인
{DOMAIN}-{SUBDOMAIN}-{NNN}  // 하위 도메인 있는 경우
```

### 코드표 (현 코드 기준)

| 도메인 | 접두어 | 실제 코드 |
|--------|--------|----------|
| `chart.option` | `CHO` | `CHO-001`(옵션 없음), `CHO-002`(미지원 차트 타입), `CHO-003`(잘못된 dataMapping), `CHO-004`(잘못된 styleOption), `CHO-409`(이미 존재) |
| `datasource` | `DS` | `DS-001`(요청 JSON 해석 불가), `DS-002`(미지원 소스 타입), `DS-003`(업로드 파일 필요), `DS-004`(파일 저장 실패) |
| `project` | `PRJ` | `PRJ-001`(프로젝트 없음) — 현재 `chart.option`의 `ChartOptionErrorCode`에 정의됨 |
| `auth`(공통 인증) | `AUT` | `AUT-015`(403 권한 없음), `AUT-016`(401 인증 필요) — `GlobalExceptionHandler`가 발급 |

> ⚠️ `project.ProjectErrorCode`는 프로젝트가 아니라 **채팅방 코드(`CHT-001~003`)**를 담고 있다(타 출처 잔재, 정리 대상).

### 공통 코드 (`GlobalExceptionHandler`)

| 코드 | HTTP | 용도 |
|------|------|------|
| `COMMON-VALIDATION-FAILED` | 400 | `@Valid` 검증 실패 (필드별 `errors` 포함) |
| `COMMON-BAD-REQUEST` | 400 | 파라미터 누락·타입 불일치·본문 파싱 실패 |
| `FILE_SIZE_EXCEEDED` | 413 | 업로드 용량 초과 |
| `INTERNAL_ERROR` | 500 | 처리되지 않은 예외 |

### 에러코드 추가 방법
1. 해당 도메인의 `XxxErrorCode` enum에 상수 추가
2. 코드 문자열은 위 코드표 prefix + 시퀀스 번호 순서대로
3. **개별 Exception 클래스 신규 생성 금지** — 공통 예외 타입 사용 (→ [`code.md`](code.md))

## 2. 예외 처리 구조

### 공통 예외 타입 (`global.domain.common.error.exception`)

| 클래스 | HTTP Status | 용도 |
|--------|-------------|------|
| `NotFoundException` | 404 | 리소스 없음 |
| `ValidationException` | 400 | 입력값 검증 실패 |
| `UnauthorizedException` | 401 | 인증 실패 |
| `ForbiddenException` | 403 | 권한 없음 |
| `ConflictException` | 409 | 중복 / 충돌 |
| `TooManyRequestsException` | 429 | 요청 한도 초과 |
| `ExternalServiceException` | 502 | 외부 서비스 연동 실패 |

> 모두 추상 클래스 `global.domain.common.DomainException`을 상속한다. 글로벌에 422 공통 타입이 없어 `chart.option`은 도메인 전용 `ChartOptionUnprocessableEntityException`(422)을 두고 `CHO-002/003/004`에 쓴다.

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

### 에러 응답 포맷 (ApiErrorResponse)

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

> `errors`는 `@Valid` 검증 실패(`COMMON-VALIDATION-FAILED`)일 때만 필드별 항목이 채워지고, 그 외에는 빈 배열이다.
