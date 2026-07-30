# MAC 모니터링 스택 (Loki + Prometheus + Grafana)

로컬에서 MAC 앱의 **로그(Loki)** 와 **메트릭(Prometheus)** 을 수집하고, **Grafana** 대시보드로 한눈에 보는 세트입니다.
Docker 컨테이너 3개로 뜨고, 앱은 로컬(`local` 프로파일)에서 그대로 실행합니다.

```
[Spring Boot 앱 :8080]  ──메트릭(pull)──▶  [Prometheus :9090]
   (로컬 실행)          ──로그(push)────▶  [Loki :3100]        ──▶ [Grafana :3000]
```

---

## 0. 사전 준비 (한 번만)

- **Docker Desktop** 설치 및 실행 중일 것
- 이 저장소를 `git pull` 로 최신 develop 반영

확인:
```bash
docker --version
docker compose version
```

---

## 1. 모니터링 스택 기동

프로젝트 루트에서:

```bash
docker compose -f docker-compose.monitoring.yml up -d
```

컨테이너 3개(`projectmac-loki`, `projectmac-prometheus`, `projectmac-grafana`)가 뜹니다.

상태 확인:
```bash
docker compose -f docker-compose.monitoring.yml ps
```

## 2. 앱 실행 (local 프로파일 필수)

`local`/`dev` 프로파일에서만 로그가 Loki로 전송됩니다.

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

> IDE로 실행할 때도 VM 옵션/Active profiles를 `local` 로 지정하세요.

## 3. Grafana 접속

| 항목 | 값 |
|------|-----|
| URL | http://localhost:3000 |
| 계정 | `admin` / `admin` (최초 로그인 시 비번 변경 요구 → Skip 가능) |

접속 후 좌측 **Dashboards → MAC 폴더 → "MAC Overview"** 를 열면 끝입니다.
datasource(Loki·Prometheus)와 대시보드는 **자동 프로비저닝**되므로 별도 설정이 필요 없습니다.

---

## 대시보드 구성 (MAC Overview)

| 섹션 | 패널 |
|------|------|
| 상태 | 앱 UP/DOWN, 요청 처리율, 5xx 에러율, 평균 응답시간 |
| HTTP | 상태코드별 처리율, URI별 응답시간(평균·최대) |
| JVM / DB | Heap 메모리, CPU 사용률, HikariCP 커넥션 |
| 로그(Loki) | 애플리케이션 로그 실시간 조회, 레벨별 발생량 |

로그를 직접 쿼리하려면 Grafana **Explore → Loki** 에서:
```logql
{app="projectmac"}                          # 전체
{app="projectmac", level="ERROR"}           # 에러만
{app="projectmac"} |= "AlertService"        # 특정 문자열 포함
```

---

## 접속 포인트 요약

| 서비스 | URL | 용도 |
|--------|-----|------|
| Grafana | http://localhost:3000 | 대시보드 (여기만 보면 됨) |
| Prometheus | http://localhost:9090 | 메트릭 원본/쿼리 |
| Loki | http://localhost:3100 | 로그 저장소 (API) |
| 앱 메트릭 | http://localhost:8080/actuator/prometheus | Prometheus 스크랩 대상 |

---

## 종료 / 정리

```bash
# 컨테이너만 중지 (수집 데이터 유지)
docker compose -f docker-compose.monitoring.yml down

# 데이터까지 완전 삭제 (대시보드 편집분 제외, 수집된 로그·메트릭 삭제)
docker compose -f docker-compose.monitoring.yml down -v
```

---

## 트러블슈팅

**Grafana에 메트릭이 안 보임**
- 앱이 `8080` 포트로 떠 있는지 확인 (`http://localhost:8080/actuator/prometheus` 접속 시 텍스트가 나와야 함)
- Prometheus 타겟 상태 확인: http://localhost:9090/targets → `projectmac` 가 **UP** 인지
- Windows/Mac Docker Desktop에서는 `host.docker.internal` 이 자동 동작. Linux면 compose의 prometheus 서비스에
  `extra_hosts: ["host.docker.internal:host-gateway"]` 를 추가해야 함

**Grafana에 로그가 안 보임**
- 앱을 `--spring.profiles.active=local` (또는 `dev`)로 실행했는지 확인 (prod는 Loki 미전송)
- Loki 준비 상태: http://localhost:3100/ready 가 `ready` 반환하는지

**포트 충돌 (3000/9090/3100/8080 이미 사용 중)**
- 기존 프로세스를 끄거나, `docker-compose.monitoring.yml` 의 `ports` 좌측 값을 변경
  (예: `"3001:3000"`). 앱 포트 8080을 바꿨다면 `monitoring/prometheus/prometheus.yml` 의 타겟도 함께 수정

---

## 구조 / 파일

```
docker-compose.monitoring.yml            # 스택 3개 컨테이너 정의
monitoring/
├── loki/loki-config.yml                 # Loki 설정 (파일시스템 저장)
├── prometheus/prometheus.yml            # 스크랩 대상: host:8080/actuator/prometheus
└── grafana/provisioning/
    ├── datasources/datasources.yml      # Loki·Prometheus 자동 연결
    └── dashboards/
        ├── dashboards.yml               # 대시보드 프로바이더
        └── mac-overview.json            # MAC Overview 대시보드
```

앱 연동은 아래 파일에 반영되어 있습니다.
- `build.gradle` — actuator, micrometer-prometheus, loki-logback-appender 의존성
- `src/main/resources/application.yml` — actuator `prometheus` 엔드포인트 노출
- `src/main/resources/logback-spring.xml` — `LOKI` appender (local/dev 프로파일)

---

## 주의

- 이 스택은 **로컬 개발용**입니다. Grafana 기본 계정(admin/admin)과 익명 접근 설정은 운영에 그대로 쓰지 마세요.
- 운영(prod)에서는 Loki 전송이 비활성(로그는 파일 appender)이고, P6Spy도 dev/staging 한정입니다.
