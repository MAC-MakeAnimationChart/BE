package com.mac.projectmac.global.logging.presentation.api.dto;

import com.mac.projectmac.global.logging.infrastructure.entity.AppLog;
import lombok.Getter;

import java.time.Instant;

@Getter
public class AppLogResponse {

    private final Long logId;
    private final String level;
    private final String className;
    private final String logMessage;
    private final String stackTrace;
    private final String requestId;
    private final Long userId;
    private final Instant createdAt;

    public AppLogResponse(AppLog appLog, boolean includeStackTrace) {
        this.logId = appLog.getId();
        this.level = appLog.getLevel().name();
        this.className = appLog.getClassName();
        this.logMessage = appLog.getLogMessage();
        this.stackTrace = includeStackTrace ? appLog.getStackTrace() : null;
        this.requestId = appLog.getRequestId();
        this.userId = appLog.getUserId();
        this.createdAt = appLog.getCreatedAt();
    }
}
