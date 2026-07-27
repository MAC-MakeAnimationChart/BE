# DataSource Domain

프로젝트에 종속된 데이터소스 파일(업로드 원본)을 관리한다. **프로젝트 1개당 데이터소스 1개(1:1)** 이며, 소유·삭제 생명주기는 프로젝트를 따른다.

## 주요 역할

- 프로젝트별 데이터소스 파일을 업로드/교체한다(create-or-replace, 같은 행 갱신).
- 파일을 스토리지(GCS/로컬)에 저장하고 메타데이터를 관리한다.
- 데이터소스를 조회하고, 하드 삭제한다(행 + 물리 파일).
- 접근 시 프로젝트 소유권(`project.user_id`)을 검증한다(없음 404 / 남의 것 403).

## 주요 모델 (용어)

| 모델 | 설명 |
| --- | --- |
| `DataSource` | 프로젝트 1:1 데이터소스 메타(`projectId`·`fileName`·`storedFileName`·`fileUrl`·`filePath`·`fileSize`·`mimeType`). 소유자는 `project.user_id`로 판단 |
| `StoredFile` | 스토리지에 저장된 파일 결과 정보(경로·URL·크기·MIME 타입) |

## 경계

다른 도메인과의 연동은 `docs/CONTEXT-MAP.md`를 단일 소스로 본다.

> 패키지 구조·서비스·API는 코드/Swagger를 본다. 깊은 설명은 오너 `.ai/local/`.
