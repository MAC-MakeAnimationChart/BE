package com.mac.projectmac.global.logging.infrastructure.p6spy;

import com.mac.projectmac.global.logging.config.LoggingProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlowQueryLogger {

    private final LoggingProperties loggingProperties;
    private long slowQueryThresholdMs;

    @PostConstruct
    public void init() {
        this.slowQueryThresholdMs = loggingProperties.getSlowMethod().getServiceThresholdMs();
    }

    public void logIfSlow(long elapsedMs, String sql) {
        if (elapsedMs >= slowQueryThresholdMs) {
            log.warn("[SlowQueryLogger] 슬로우 쿼리 감지 - executionMs={}, sql={}",
                    elapsedMs, sql);
        }
    }
}
