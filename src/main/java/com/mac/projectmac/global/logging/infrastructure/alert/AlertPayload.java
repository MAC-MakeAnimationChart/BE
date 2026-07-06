package com.mac.projectmac.global.logging.infrastructure.alert;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class AlertPayload {

    private final Instant occurredAt;
    private final String requestMethod;
    private final String requestUri;
    private final String userId;
    private final String exceptionClass;
    private final String message;
    private final String stackTrace;
}
