package com.mac.projectmac.global.logging.presentation.api.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class HealthResponse {

    private String status;
    private String db;
    private Instant timestamp;
}
