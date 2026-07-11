package com.mac.projectmac.problem.dataset.infrastructure.storage;

import com.mac.projectmac.problem.dataset.application.port.StoreProblemDatasetFilePort;
import com.mac.projectmac.global.infrastructure.storage.GcpStorageProperties;
import com.mac.projectmac.global.infrastructure.storage.GcsClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("problemDatasetStorageConfig")
public class StorageConfig {

    /** gcp.storage.bucket 이 설정된 경우(운영) GCS 어댑터 사용. */
    @Bean
    @ConditionalOnProperty(prefix = "gcp.storage", name = "bucket")
    public StoreProblemDatasetFilePort gcsProblemDatasetStorage(
            GcsClient gcsClient,
            GcpStorageProperties properties
    ) {
        return new GcsProblemDatasetStorage(gcsClient, properties.getStorage().getProblemDatasetPrefix());
    }

    /** bucket 미설정(로컬/테스트) 시 로컬 폴백. */
    @Bean
    @ConditionalOnMissingBean(StoreProblemDatasetFilePort.class)
    public StoreProblemDatasetFilePort localProblemDatasetStorage() {
        return new LocalProblemDatasetStorage();
    }
}
