package com.mac.projectmac.global.logging.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "mac.logging")
public class LoggingProperties {

    private int bodyMaxLength = 2000;
    private SlowMethod slowMethod = new SlowMethod();
    private List<String> excludeUrls = List.of();

    @Getter
    @Setter
    public static class SlowMethod {
        private long controllerThresholdMs = 500;
        private long serviceThresholdMs = 1000;
    }
}
