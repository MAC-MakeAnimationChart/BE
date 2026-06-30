package com.mac.projectmac.auth.domain.exception;

import com.mac.projectmac.global.domain.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    USER_NOT_FOUND          ("AUT-001", "존재하지 않는 유저입니다."),
    USER_EMAIL_DUPLICATED   ("AUT-002", "이미 사용 중인 이메일입니다."),
    USER_NICKNAME_DUPLICATED("AUT-003", "이미 사용 중인 닉네임입니다."),
    USER_ACCOUNT_BANNED     ("AUT-004", "정지된 계정입니다."),
    USER_ALREADY_BANNED     ("AUT-005", "이미 정지된 계정입니다."),

    AUTH_PASSWORD_MISMATCH  ("AUT-006", "비밀번호가 올바르지 않습니다."),
    AUTH_UNAUTHENTICATED    ("AUT-016", "인증이 필요합니다."),
    AUTH_TOKEN_EXPIRED      ("AUT-007", "인증 토큰이 만료되었습니다."),
    AUTH_REFRESH_NOT_FOUND  ("AUT-008", "Refresh Token이 존재하지 않습니다."),
    AUTH_REFRESH_MISMATCH   ("AUT-009", "Refresh Token이 일치하지 않습니다."),
    AUTH_FORBIDDEN          ("AUT-015", "접근 권한이 없습니다."),

    INVALID_CHECK_TYPE      ("AUT-010", "중복 확인 타입은 email 또는 nickname이어야 합니다."),
    INVALID_STATUS_VALUE    ("AUT-011", "상태값은 BANNED 또는 ACTIVE이어야 합니다.");

    private final String code;
    private final String message;
}
