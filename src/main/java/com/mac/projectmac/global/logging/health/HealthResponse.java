package com.mac.projectmac.global.logging.health;

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
