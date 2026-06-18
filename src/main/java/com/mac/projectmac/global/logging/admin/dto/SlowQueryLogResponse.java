package com.mac.projectmac.global.logging.admin.dto;

import com.mac.projectmac.global.logging.admin.domain.SlowQueryLog;
import lombok.Getter;

import java.time.Instant;

@Getter
public class SlowQueryLogResponse {

    private final Long queryId;
    private final String sqlQuery;
    private final int executionMs;
    private final String requestId;
    private final Instant createdAt;

    public SlowQueryLogResponse(SlowQueryLog log) {
        this.queryId = log.getId();
        this.sqlQuery = log.getSqlQuery();
        this.executionMs = log.getExecutionMs();
        this.requestId = log.getRequestId();
        this.createdAt = log.getCreatedAt();
    }
}
