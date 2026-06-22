# 코드 작성 컨벤션

> 예외·응답을 코드로 옮길 때의 횡단 규칙 + Swagger 작성 가이드.
> 관련: 에러코드/예외 [`exception.md`](exception.md), 성공 응답 [`response.md`](response.md).

## 1. 횡단 규칙

### 개별 Exception 클래스 신규 생성 금지

도메인마다 예외 클래스를 만들지 말고 공통 예외 타입([`exception.md`](exception.md))에 `XxxErrorCode`를 넘긴다.

```java
// Good
throw new NotFoundException(ChartOptionErrorCode.CHART_OPTION_NOT_FOUND);

// Bad — 개별 클래스 만들지 말 것
throw new ChartOptionNotFoundException();
```

### ResponseCode / ResponseMessage 작성 규칙

- 각 도메인 `presentation/api/`에 `XxxResponseCode`(코드 상수) + `XxxResponseMessage`(메시지 상수) 두 클래스를 둔다.
- 상수명은 동사 없이 행위/상태 결과로 표현: `OK`, `SAVED`, `CREATED` 등.
- 코드 문자열 포맷: `{접두어}-{HTTP상태}` (현 코드 기준 — 예: `CHO-200`, `CHO-201`).
- 생성자 `private` — 인스턴스화 금지.

```java
public class ChartOptionResponseCode {
    private ChartOptionResponseCode() {}

    public static final String OK      = "CHO-200";
    public static final String SAVED   = "CHO-200";
    public static final String CREATED = "CHO-201";
}

public class ChartOptionResponseMessage {
    private ChartOptionResponseMessage() {}

    public static final String OK      = "차트 옵션 조회 성공";
    public static final String SAVED   = "차트 옵션 저장 성공";
    public static final String CREATED = "차트 옵션 생성 성공";
}
```

## 2. Swagger 작성 가이드

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
