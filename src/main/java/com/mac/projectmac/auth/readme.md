## 패키지 구조

```
auth/
├── business/
│   ├── entity/
│   │   └── User.java               # 유저 엔티티 (role, status, socialType 포함)
│   ├── exception/
│   │   └── AuthErrorCode.java      # 인증 관련 에러 코드 (ErrorCode 구현체)
│   └── repository/
│       └── TokenRepository.java    # Redis 토큰 저장소 인터페이스
│
├── application/
│   └── service/
│       └── AuthService.java        # 회원가입, 로그인, 로그아웃, 토큰 재발급
│
├── infrastructure/
│   ├── config/
│   │   ├── SecurityConfig.java     # Spring Security 필터 체인, URL 접근 권한
│   │   ├── RedisConfig.java        # RedisTemplate 설정
│   │   └── WebConfig.java          # CORS 설정
│   ├── persistence/
│   │   └── UserJpaRepository.java  # Spring Data JPA 유저 저장소
│   ├── redis/
│   │   └── RedisTokenRepository.java  # TokenRepository Redis 구현체
│   └── security/
│       ├── JwtTokenProvider.java       # JWT 생성, 검증, 파싱
│       └── JwtAuthenticationFilter.java  # 요청마다 JWT 검증 후 SecurityContext 설정
│
└── presentation/
    └── api/
        ├── AuthController.java     # /api/v1/auth/** 엔드포인트
        ├── request/
        │   ├── RegisterRequest.java
        │   └── LoginRequest.java
        └── response/
            ├── RegisterResponse.java
            ├── CheckResponse.java
            ├── LoginResponse.java
            └── TokenResponse.java
```

---

## API 목록

| Method | URL | 인증 | 설명 |
|--------|-----|------|------|
| GET | `/api/v1/auth/check` | 불필요 | 이메일/닉네임 중복 확인 (`?type=email&value=...`) |
| POST | `/api/v1/auth/register` | 불필요 | 회원가입 |
| POST | `/api/v1/auth/login` | 불필요 | 로그인 (Access Token 반환, Refresh Token 쿠키 설정) |
| POST | `/api/v1/auth/reissue` | 불필요 | 토큰 재발급 (RTR 방식) |
| POST | `/api/v1/auth/logout` | Access Token | 로그아웃 |

---

## 인증 흐름

```
로그인
  └─ Access Token (1시간, Authorization 헤더)
  └─ Refresh Token (7일, HttpOnly 쿠키)

토큰 재발급 (RTR)
  └─ Refresh Token 쿠키 전달
  └─ Redis 저장값과 일치 확인
  └─ 불일치 시 → 해당 유저 토큰 전체 폐기 (탈취 감지)
  └─ 일치 시 → 기존 Refresh Token 삭제 후 신규 발급

로그아웃
  └─ Refresh Token 삭제 (Redis)
  └─ Access Token 블랙리스트 등록 (Redis, 잔여 만료 시간 동안 유지)
```
