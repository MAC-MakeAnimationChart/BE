# Chart Option 구현 메모

## 현재 구현 범위

- `POST /api/v1/projects/{projectId}/chart-option`으로 차트 옵션 최초 행을 생성한다.
- 최초 생성 시 `projectId`, `chartType`만 저장하고 `dataMapping`, `styleOption`은 `null`로 둔다.
- `BAR`, `BAR_RACE`, `BAR_GROUPED`, `BAR_STACKED`, `LINE`, `AREA`, `DONUT`, `PIE`, `WORD_CLOUD`, `METRIC_CARD`, `TREEMAP`, `SCATTER`를 API 허용 값으로 둔다.
- 와이어프레임에서 준비 중인 `TREEMAP`, `SCATTER`도 추후 개발 계획이 있으므로 백엔드에서는 막지 않는다.
- 완전히 정의되지 않은 값이나 대소문자가 다른 값은 `422 CHO-422`로 처리한다.

## Project 엔티티 추가 시 수정 지점

현재는 프로젝트 도메인 엔티티가 없어서 `ProjectExistenceJpaAdapter`가 native query로 `project` 테이블 존재 여부만 확인한다.

프로젝트 엔티티와 repository가 생기면 아래 방향으로 교체한다.

| 현재 파일 | 수정 방향 |
| --- | --- |
| `ProjectExistenceJpaAdapter` | native query 제거 후 project repository/adapter 호출 |
| `ProjectExistencePort` | 필요하면 `ProjectQueryPort`, `ProjectOwnerPort`처럼 역할 기준으로 분리 |
| `ChartOptionJpaEntity.projectId` | 단순 ID 유지 또는 `ProjectJpaEntity` 연관관계로 전환 결정 |
| `ChartOptionCommandService.validateProjectExists` | 프로젝트 존재 검증 + 삭제 여부 검증으로 확장 |

추천은 chart option 도메인에서 project 엔티티를 직접 강하게 물지 않고, application port를 통해 `projectId` 존재/소유 여부만 확인하는 방식이다.

## User 엔티티 추가 시 수정 지점

차트 옵션은 사용자와 직접 연결되기보다 프로젝트를 통해 소유자를 판단하는 구조가 자연스럽다.

| 추가 항목 | 수정 방향 |
| --- | --- |
| `project.user_id` 연관관계 | 프로젝트 소유자 판단 기준으로 사용 |
| `RegisterChartOptionCommand` | `userId` 필드 추가 |
| `ProjectExistencePort` | `existsByProjectIdAndUserId(projectId, userId)` 또는 별도 ownership port로 변경 |
| `ChartOptionCommandService` | 프로젝트 존재 여부와 소유자 권한을 함께 검증 |

권한이 없는 프로젝트 접근은 정책에 따라 `403 AUT-015` 또는 보안상 `404 PRJ-001`로 숨김 처리할 수 있다. 팀 API 컨벤션 확정 후 하나로 통일한다.

## Security/토큰 기반 인증 추가 시 수정 지점

JWT 또는 쿠키 기반 인증이 들어오면 controller에서 인증 사용자 식별자를 받아 command로 넘긴다.

예상 변경 흐름:

```java
public ResponseEntity<ApiResponse<?>> registerChartOption(
        @AuthenticationPrincipal Long userId,
        @PathVariable Long projectId,
        @RequestBody RegisterChartOptionRequest request
) {
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created(
                    ChartOptionResponseCode.CREATED,
                    ChartOptionResponseMessage.CREATED,
                    ChartOptionResponse.from(registerChartOptionUseCase.register(request.toCommand(userId, projectId)))
            ));
}
```

필요한 후속 수정:

| 위치 | 수정 방향 |
| --- | --- |
| `ChartOptionController` | `@AuthenticationPrincipal` 또는 커스텀 인증 principal 사용 |
| `RegisterChartOptionRequest.toCommand` | `userId`까지 포함해 command 생성 |
| `RegisterChartOptionCommand` | `Long userId` 추가 |
| `ChartOptionCommandService` | 프로젝트 소유자 검증 추가 |
| 테스트 | `@WithMockUser` 대신 실제 principal 타입에 맞춘 보안 테스트 추가 |
| HTTP 테스트 | 실제 `ACCESS_TOKEN`, `REFRESH_TOKEN` 쿠키 사용 |

인증이 붙어도 `chart_option` 테이블에는 `user_id`를 중복 저장하지 않는 쪽을 우선 검토한다. 소유권은 `project.user_id`를 통해 판단하면 중복과 불일치 위험이 줄어든다.
