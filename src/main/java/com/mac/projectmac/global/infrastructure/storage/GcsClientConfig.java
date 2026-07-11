package com.mac.projectmac.global.infrastructure.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class GcsClientConfig {

    /** gcp.storage.bucket 이 설정된 경우(운영/로컬 GCS)에만 공통 GCS 클라이언트를 등록한다. */
    @Bean
    @ConditionalOnProperty(prefix = "gcp.storage", name = "bucket")
    public GcsClient gcsClient(GcpStorageProperties properties, ResourceLoader resourceLoader) {
        return new GcsClient(properties, resourceLoader);
    }
}
