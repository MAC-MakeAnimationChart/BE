# MAC (Make Animation Chart) — Backend

## 프로젝트 소개

MAC는 업로드한 데이터소스를 기반으로 **애니메이션 차트**를 만드는 서비스의 백엔드입니다.
프로젝트 단위로 차트 옵션(차트 타입·데이터 매핑·스타일)을 관리하고, 데이터소스 파일을 업로드·저장합니다.

Spring Boot 기반이며, 도메인별 책임 분리와 헥사고날(포트/어댑터) 구조를 적용합니다(→ [`adr/0002-hexagonal-cqrs.md`](adr/0002-hexagonal-cqrs.md)).

## 주요 기능

- 프로젝트별 차트 옵션 최초 생성·조회·저장
- 차트 타입 12종 지원(`BAR`, `BAR_RACE`, `LINE`, `AREA`, `DONUT`, `PIE`, `WORD_CLOUD`, `METRIC_CARD`, `TREEMAP`, `SCATTER` 등)
- 데이터소스 파일 업로드(멀티파트) 및 GCS/로컬 스토리지 저장
- (예정) 인증/사용자/운영(`auth`·`user`·`admin`)·프로젝트 본 도메인

## 기술 스택

- Java 17
- Spring Boot 3.5.14
- Spring Web
- Spring Security
- Spring Data JPA
- Spring Validation
- MySQL
- Google Cloud Storage (`google-cloud-storage:2.68.0`)
- Springdoc OpenAPI Swagger (`2.8.6`)
- Gradle
- (test) H2, Spring Security Test, JUnit

> 참고: Redis·Thymeleaf·JWT·WebFlux·Mail 등은 현재 의존성에 없다. 인증은 Spring Security만 설정돼 있다.

## 프로젝트 구조

```text
src/main/java/com/mac/projectmac/
 ├─ chart/option/   # 차트 옵션 (presentation/application/domain/infrastructure)
 ├─ datasource/     # 데이터소스 업로드·저장 (api/application/domain/infrastructure)
 ├─ project/        # 프로젝트 — 현재 ProjectErrorCode만 존재(도메인 모델 미구현)
 ├─ admin/          # (예정, readme 플레이스홀더)
 ├─ auth/           # (예정, readme 플레이스홀더)
 ├─ user/           # (예정, readme 플레이스홀더)
 └─ global/         # 공통 응답·예외, 파일 스토리지, 설정, 로깅
```

## 계층 구조

주요 도메인은 헥사고날 4계층으로 구성합니다(→ [`adr/0002-hexagonal-cqrs.md`](adr/0002-hexagonal-cqrs.md)).

```text
domain-name/
 ├─ presentation/api/   # API 요청/응답 (Controller, ResponseCode/Message, request/response DTO)
 ├─ application/        # usecase(입력 포트), command, port(출력 포트), service(구현)
 ├─ domain/             # Domain Model, Repository(Port), Domain Exception/ErrorCode
 └─ infrastructure/     # JPA Entity, Repository Adapter, 외부 연동(스토리지 등)
```

> `datasource`는 `presentation` 대신 `api` 패키지명을 쓰고, `global`은 `api/domain/infrastructure` 구성이다.

## 주요 API 경로

### Chart Option

| 기능 | Method | URL |
|---|---|---|
| 차트 옵션 조회 | GET | `/api/v1/projects/{projectId}/chart-option` |
| 차트 옵션 최초 생성 | POST | `/api/v1/projects/{projectId}/chart-option` |
| 차트 옵션 저장/수정 | PUT | `/api/v1/projects/{projectId}/chart-option` |

### DataSource

| 기능 | Method | URL |
|---|---|---|
| 데이터소스 업로드 | POST | `/api/v1/data-sources` (`multipart/form-data`: `data`(JSON) + `file`) |

## API 문서

애플리케이션 실행 후 Swagger UI에서 확인할 수 있습니다.

```text
http://localhost:8080/swagger-ui/index.html
```

## 실행 전 준비 사항

로컬 실행에는 MySQL과 로컬 프로파일 설정이 필요합니다. 로컬 설정 파일은 `.gitignore` 처리되어 있으므로 직접 생성합니다.

```text
src/main/resources/application-local.yml
```

### application-local.yml 예시

```yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mac_db?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8&serverTimezone=Asia/Seoul
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: your_username
    password: your_password

  jpa:
    hibernate:
      ddl-auto: update
    open-in-view: false

  sql:
    init:
      mode: never
```

### 파일 스토리지(GCS) 설정

`application.yml`은 GCS 키를 환경변수로 주입받습니다. **`gcp.storage.bucket`이 없으면 `LocalFileStorage`로 폴백**하여 로컬(`uploads/`)에 저장하므로, 로컬 개발에는 별도 GCS 설정 없이도 동작합니다.

| 환경변수 | 용도 |
|---|---|
| `GCP_PROJECT_ID` | GCP 프로젝트 ID |
| `GCP_CREDENTIALS_LOCATION` | 서비스 계정 키 위치 (`file:...`) |

> 업로드 용량 한도는 50MB(`spring.servlet.multipart.max-file-size`).

## 실행 방법

### Mac / Linux

```bash
./gradlew bootRun
```

### Windows

```bash
.\gradlew.bat bootRun
```

## 테스트 실행

```bash
./gradlew test        # Mac / Linux
.\gradlew.bat test    # Windows
```

## 주요 문서

프로젝트 문서는 `docs` 디렉토리에서 관리합니다.

```text
docs/
 ├─ README.md         # (이 문서) 프로젝트 개요·실행
 ├─ CONTEXT-MAP.md    # 교차 도메인 경계·관계 (단일 소스)
 ├─ DOC-SYSTEM.md     # 문서 체계(owns-what)·README 슬림화 가이드
 ├─ CONVENTION.md     # 응답·예외·에러코드·Swagger 규약(통합)
 ├─ convention/       # code.md / exception.md / response.md (분할)
 └─ adr/              # 0001~0005 팀 결정 기록
```

| 문서 | 설명 |
|---|---|
| [`CONTEXT-MAP.md`](CONTEXT-MAP.md) | 도메인 경계·관계의 단일 소스 |
| [`DOC-SYSTEM.md`](DOC-SYSTEM.md) | 어떤 정보가 어디에 적히는지(owns-what) |
| [`CONVENTION.md`](CONVENTION.md) | API 응답·예외·에러코드·Swagger 컨벤션 |
| [`convention/`](convention/) | 컨벤션 분할 문서(코드/예외/응답) |
| [`adr/`](adr/) | 결정과 이유(append-only) |

## 형상관리 기준

- 기준 브랜치의 최신 변경사항을 반영한 뒤 작업합니다.
- 기능 단위로 브랜치를 생성합니다.
- PR 생성 전 실행 또는 테스트를 확인합니다.
- 다른 팀원의 기능 코드에 불필요한 변경을 하지 않습니다.
- 문서 변경이 필요하면 `docs` 하위 문서를 함께 수정합니다.

## gitignore 처리된 항목 요약

| 항목 | 이유 |
|---|---|
| `.idea`, `.vscode/` | IDE 개인 설정 |
| `build/`, `.gradle`, `bin/`, `out/` | 빌드/캐시 결과물 |
| `src/main/resources/application-local.yml` | DB 비밀번호 등 로컬 설정 |
| `AGENTS.md`, `AGENT.MD`, `API_WORKLOG.MD`, `.ai/` | 로컬 AI 프롬프트·작업메모 |
| `uploads/` | `LocalFileStorage` 폴백 저장 경로 |

## 주의사항

- 민감한 값(DB 비밀번호, GCS 인증 등)은 `application-local.yml` 또는 환경변수로만 주입하고 커밋하지 않습니다.
- Windows와 Linux는 파일명 대소문자 처리 방식이 다릅니다. 패키지명·파일명을 정확히 작성합니다.
