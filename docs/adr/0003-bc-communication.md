# BC 간 통신: 포트(application service) 경유 + 도메인 이벤트, SpringData 직접 참조 금지

> 상태: **accepted** (강사 조평훈 확인 — 팀 전체 규칙).

## 맥락

모노레포라 한 도메인이 다른 도메인의 JPA 리포지토리/엔티티에 직접 손댈 수 있다. 그러면 경계가
무너지고 변경이 전파된다. 궁극 목표는 BC 단위 독립(MSA 분리 가능). BC 간 통신 방식을 정해야 한다.

## 결정

- **동기 조회/명령(데이터가 필요)**: 상대 BC의 `application.service`(포트)를 호출한다. **상대 BC의 SpringData 리포지토리·엔티티를 직접 참조하지 않는다.**
- **부수적 후속 작업**: 도메인 이벤트로 느슨하게 연결한다. (예: 메인 작업이 끝난 뒤 분리 가능한 후속 처리를 이벤트로 발행하고 다른 BC가 구독)
- **포트 DTO는 호출하는 BC 소유**: 포트가 정의하는 record(DTO)는 자기 모듈 것. 상대 BC의 도메인 객체를 그대로 받지 말고 자기 모듈 record로 변환한다.
- **이벤트로 조회 금지**: `publishEvent()`는 void 반환. 리턴값이 필요하면 포트.

### Port vs Event 판단 기준

순서대로 하나라도 Yes → **Port/Adapter**, 셋 다 No → **Event**.

1. 리턴값이 필요한가?
2. 이 작업이 실패하면 메인 작업도 실패해야 하는가?
3. 하나의 트랜잭션 안에서 일어나야 하는가?

## 근거

- `chart.option`이 `project` 존재 확인을 `ProjectExistencePort`(출력 포트)로 추상화한다 — 포트로 경계를 분리한 사례.
- `datasource`는 파일 저장을 자기 모듈 record(`CreateDataSourceCommand` 등)로 다루고, 스토리지 접근을 `StoreFilePort`로 격리한다.
- 도메인 이벤트는 아직 사용처가 없다(향후 후속 처리 분리가 필요해지면 적용).

> ⚠️ 알려진 위반: `ProjectExistenceJpaAdapter`는 상대 BC의 application service를 거치지 않고 `project` 테이블에 native query를 직접 날린다. `project` BC의 엔티티/리포지토리가 생기면 포트 호출로 교체 예정 — 출력 포트(`ProjectExistencePort`)는 이미 분리돼 있어 어댑터만 갈아끼우면 된다. 본 ADR은 결정만 기록한다.
