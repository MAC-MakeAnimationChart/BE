# 성공 응답 컨벤션 (ApiResponse)

> 성공 응답 포맷·ResponseCode/Message 규칙. 결정·이유는 [`docs/adr/0004-error-response-standard.md`](../adr/0004-error-response-standard.md).
> 관련: 에러/예외는 [`exception.md`](exception.md), 코드 작성 규칙(private 생성자·네이밍)은 [`code.md`](code.md).

> 모든 컨트롤러가 `ApiResponse`로 통일돼 있다. 신규 컨트롤러도 `ApiResponse`만 쓴다.

## 사용법

`ApiResponse`(`global.api.common`) 정적 팩토리:

| 메서드 | 용도 |
|--------|------|
| `success(code, message, data)` | 조회·수정 (200, 데이터 있음) |
| `success(code, message)` | 데이터 없는 200 |
| `created(code, message, data)` | 생성 (201) |

```java
// 조회 (200)
return ResponseEntity.ok(ApiResponse.success(
    ChartOptionResponseCode.OK,
    ChartOptionResponseMessage.OK,
    ChartOptionDetailResponse.from(getChartOptionUseCase.getByProjectId(projectId))
));

// 저장/수정 (200)
return ResponseEntity.ok(ApiResponse.success(
    ChartOptionResponseCode.SAVED,
    ChartOptionResponseMessage.SAVED,
    ChartOptionSaveResponse.from(updateChartOptionUseCase.update(request.toCommand(projectId)))
));

// 생성 (201)
return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(
    ChartOptionResponseCode.CREATED,
    ChartOptionResponseMessage.CREATED,
    ChartOptionResponse.from(registerChartOptionUseCase.register(request.toCommand(projectId)))
));

// 본문 없는 성공(204)은 ApiResponse 없이 ResponseEntity.noContent().build()
```

## ResponseCode / ResponseMessage

각 도메인 `presentation/api/` 패키지에 `XxxResponseCode` + `XxxResponseMessage` 두 클래스를 둔다(상수 모음).

```
chart/option/presentation/api/
├── ChartOptionController.java
├── ChartOptionResponseCode.java     ← 성공 코드 상수 (OK=CHO-200, SAVED=CHO-200, CREATED=CHO-201)
└── ChartOptionResponseMessage.java  ← 성공 메시지 상수
```

작성 규칙(상수 네이밍·private 생성자)은 [`code.md`](code.md).

> ⚠️ 현 코드 기준: 코드 문자열은 `{DOMAIN}-{ACTION}`(예: `CHO-RETRIEVED`)이 아니라 **`{DOMAIN}-{HTTPSTATUS}`**로 쓴다 (`chart.option` → `CHO-200`/`CHO-201`, `datasource` → `DS-201`).

## 성공 응답 포맷

```json
{
  "timestamp": "2026-05-21T10:00:00Z",
  "status": 200,
  "code": "CHO-200",
  "message": "차트 옵션 조회 성공",
  "data": { ... }
}
```
