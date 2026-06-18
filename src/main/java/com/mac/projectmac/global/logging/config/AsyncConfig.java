package com.mac.projectmac.global.logging.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
@RequiredArgsConstructor
public class AsyncConfig {

    private final AlertProperties alertProperties;

    @Bean(name = "alertExecutor")
    public Executor alertExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(alertProperties.getThreadPool().getCoreSize());
        executor.setMaxPoolSize(alertProperties.getThreadPool().getMaxSize());
        executor.setQueueCapacity(alertProperties.getThreadPool().getQueueCapacity());
        executor.setThreadNamePrefix("alert-");
        executor.initialize();
        return executor;
    }
}
