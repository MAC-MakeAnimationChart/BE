package com.mac.projectmac.global.logging.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "mac.alert")
public class AlertProperties {

    private boolean enabled = true;
    private int throttleMinutes = 1;
    private ThreadPool threadPool = new ThreadPool();
    private Discord discord = new Discord();
    private Slack slack = new Slack();
    private List<String> notifyLevels = List.of("ERROR");
    private List<String> maskFields = List.of("password", "token", "cardNumber", "Authorization");

    @Getter
    @Setter
    public static class ThreadPool {
        private int coreSize = 2;
        private int maxSize = 5;
        private int queueCapacity = 100;
    }

    @Getter
    @Setter
    public static class Discord {
        private String webhookUrl = "";
    }

    @Getter
    @Setter
    public static class Slack {
        private String webhookUrl = "";
    }
}
