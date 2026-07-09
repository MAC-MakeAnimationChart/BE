
## 패키지 구조

```
admin/
├── application/
│   └── service/
│       └── AdminService.java       # 계정 상태 변경 (BANNED / ACTIVE)
│
└── presentation/
    └── api/
        ├── AdminController.java    # /api/v1/admin/** 엔드포인트
        ├── request/
        │   └── AccountStatusRequest.java
        └── response/
            └── AccountStatusResponse.java
```

---

## API 목록

| Method | URL | 인증 | 설명 |
|--------|-----|------|------|
| PATCH | `/api/v1/admin/users/{userId}/status` | ADMIN | 계정 상태 변경 |

---

## 계정 상태 변경 동작

```
BANNED 처리
  └─ User.status → BANNED
  └─ 해당 유저 Redis 활성 토큰 전체 즉시 폐기

ACTIVE 복구
  └─ User.status → ACTIVE
```

> ADMIN 권한 없이 접근 시 403 반환
