# 도메인 구조: 헥사고날(포트/어댑터) + command/usecase 분리

> 상태: **accepted** (chart 하위 도메인(option) 분할은 의도 — 오너 확인).

## 맥락

MAC는 여러 도메인이 한 모노레포에 있다. 도메인 간 결합을 낮추고 외부 연동(JPA, GCS 등)을
교체 가능하게 두기 위해 도메인 내부 구조의 표준이 필요하다.

## 결정

각 도메인은 `presentation → application → domain → infrastructure` 4계층(헥사고날 포트/어댑터)으로 구성한다.
`application` 내부는 `usecase`(입력 포트) / `command` / `port`(출력 포트) / `service`(구현)로 나눠
쓰기 흐름과 외부 의존을 포트로 격리한다(부분적 CQRS).

## 근거

- `chart.option`·`datasource` 모두 4계층(`presentation/application/domain/infrastructure`) + `application/{usecase,command,port,service}` 구조를 따른다.
- 포트로 격리하면 BC 간 통신(→ ADR-0003)과 infra 교체(스토리지 `GCS↔Local`, `ProjectExistencePort`의 native query↔리포지토리)가 쉬워진다.

## chart 하위 도메인 분할 (의도된 설계)

- `chart`는 단일 4계층이 아니라 하위 기능별 도메인(현재 `option`, 이후 차트 종류별 확장 여지)으로 나뉜다.
- **의도된 선택**(오너 확인). 각 하위 도메인은 자체적으로 4계층 헥사고날 구조를 갖는다 → `chart`는 "서브 BC들의 묶음"이고, 본 ADR의 헥사고날 규칙은 각 하위 도메인 단위로 적용된다.
- 하위 도메인 간 통신도 ADR-0003 규칙을 따르는 것이 이상적이나, 같은 부모 BC라 분리 우선순위는 낮다.
