# Context Map — MAC (Make Animation Chart)

모노레포에 들어있는 도메인(bounded context)들이 **무엇을 소유하고, 서로 어떻게 통신하는지**의 지도다.
각 도메인의 용어·역할 상세는 해당 도메인 `readme.md`, "왜 이렇게 통신하나"의 결정은 `docs/adr/`를 본다.

> 이 파일은 **교차 도메인 경계**의 단일 소스다. 도메인별 README는 자기 경계를 여기로 참조만 한다(중복 금지).

## Contexts

| 도메인 | 소유(한 줄) | 상태 |
|--------|------------|------|
| `chart.option` | 프로젝트별 차트 옵션(차트 타입·데이터 매핑·스타일) 최초 생성·조회·저장 | 구현됨 |
| `datasource` | 프로젝트당 1개 데이터소스 파일 업로드·교체·삭제, 스토리지 저장·`DataSource` 메타 관리 | 구현됨 (프로젝트 1:1) |
| `project` | 프로젝트 | 미구현(도메인 모델·엔티티 없음, `ProjectErrorCode`만 존재) |
| `auth` | 인증·로그인 (예정) | 미구현(코드 없음, readme 플레이스홀더) |
| `user` | 사용자 계정·소유자 (예정) | 미구현(코드 없음, readme 플레이스홀더) |
| `admin` | 운영 (예정) | 미구현(코드 없음, readme 플레이스홀더) |
| `global` | 공통 인프라(공통 응답·예외, 파일 스토리지 추상화, Swagger, JPA Auditing, MDC 로깅, Clock) | 구현됨 |

> ⚠️ `project.ProjectErrorCode`는 현재 프로젝트가 아니라 **채팅방 코드(`CHT-001~003`)**를 담고 있다(타 출처에서 옮겨온 잔재). 프로젝트 본 도메인이 생기면 정리 대상.

## 주요 모델 (용어)

| 도메인 | 모델 | 설명 |
|--------|------|------|
| `chart.option` | `ChartOption` | `projectId` + `chartType` + `dataMapping`(JSON) + `styleOption`(JSON). 최초 생성 시 매핑/스타일은 `null` |
| `chart.option` | `ChartType` | `BAR, BAR_RACE, BAR_GROUPED, BAR_STACKED, LINE, AREA, DONUT, PIE, WORD_CLOUD, METRIC_CARD, TREEMAP, SCATTER` (12종) |
| `datasource` | `DataSource` | 프로젝트 1:1 데이터소스 메타(`projectId`·`fileName`·`storedFileName`·`fileUrl`·`filePath`·`fileSize`·`mimeType`). 소유자는 `project.user_id`로 판단 (별도 `ownerId` 없음), 하드 삭제 |
| `datasource` | `StoredFile` | 스토리지에 저장된 파일 결과 정보(경로·URL·크기·타입 등) |

## Relationships (의존 방향: 호출하는 쪽 → 호출되는 쪽)

> 통신 방식: **[포트]** 출력 포트(인터페이스)를 통해 다른 BC/인프라에 접근한다.
> 직접 SpringData 참조로 BC를 가로지르지 않는다 → `docs/adr/0003-bc-communication.md`.

- `chart.option` → `project` [포트] — 프로젝트 존재 확인 (`ProjectExistencePort`).
  - 현재 구현(`ProjectExistenceJpaAdapter`)은 `project` 테이블에 native query를 직접 날린다. `project` BC의 엔티티/리포지토리가 생기면 포트 호출로 교체 예정(ADR-0003 "알려진 위반").
- `datasource` → (스토리지) [포트] — 파일 저장·삭제 (`StoreDataSourceFilePort` → `GcsDataSourceStorage` / `LocalDataSourceStorage`). `gcp.storage.bucket` 미설정 시 로컬 저장으로 폴백.
- `datasource` → `project` [포트] — 프로젝트 소유권 검증 (`ProjectAccessPort`, `project_id + user_id` native query). `data_sources.project_id`는 `UNIQUE NOT NULL FK → project ON DELETE CASCADE` (1 프로젝트 : 1 데이터소스). 프로젝트 삭제 시 CASCADE 로 함께 제거.
  - ⚠️ CASCADE 삭제 시 스토리지 물리 파일은 앱 개입 없이 남아 **고아 파일** 발생(정리 배치/프로젝트 삭제 훅 필요, 백로그).
- (전 도메인) → `global` — 공통 응답/예외/인프라.
