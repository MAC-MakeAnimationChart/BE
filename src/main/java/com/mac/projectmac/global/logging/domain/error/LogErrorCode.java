package com.mac.projectmac.global.logging.domain.error;

import com.mac.projectmac.global.domain.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LogErrorCode implements ErrorCode {

    LOG_NOT_FOUND("LOG-001", "해당 로그를 찾을 수 없습니다."),
    LOG_ACCESS_FORBIDDEN("LOG-002", "관리자 권한이 필요합니다."),
    NOTIFICATION_INVALID_URL("LOG-003", "Webhook URL 형식이 올바르지 않습니다."),
    NOTIFICATION_INVALID_CHANNEL("LOG-004", "channel 값은 DISCORD/SLACK/ALL 이어야 합니다."),
    NOTIFICATION_WEBHOOK_NOT_SET("LOG-005", "해당 채널 Webhook URL이 설정되지 않았습니다."),
    NOTIFICATION_SEND_FAILED("LOG-006", "외부 Webhook 발송에 실패했습니다."),
    HEALTH_DB_DOWN("LOG-007", "DB 연결에 실패했습니다.");

    private final String code;
    private final String message;
}
