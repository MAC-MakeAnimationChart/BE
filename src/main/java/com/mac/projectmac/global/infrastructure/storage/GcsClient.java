package com.mac.projectmac.global.infrastructure.storage;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * GCS 접근 공통 클라이언트. 인증·업로드·삭제·URL 생성 등 기술 로직을 담당한다.
 * 각 모듈 어댑터는 prefix 지정과 도메인 매핑만 책임진다.
 */
@Slf4j
public class GcsClient {

    private static final String STORAGE_PUBLIC_URL = "https://storage.googleapis.com";
    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private final GcpStorageProperties properties;
    private final ResourceLoader resourceLoader;
    private volatile Storage storage;

    public GcsClient(GcpStorageProperties properties, ResourceLoader resourceLoader) {
        this.properties = properties;
        this.resourceLoader = resourceLoader;
    }

    /** content 를 {prefix}/{uuid}_{safeName} 경로로 업로드한다. */
    public GcsObject upload(String prefix, String originalFileName, String contentType, byte[] content)
            throws IOException {
        String safeName = sanitizeFileName(originalFileName);
        String storedFileName = UUID.randomUUID() + "_" + safeName;
        String objectName = buildObjectName(prefix, storedFileName);

        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucket(), objectName))
                .setContentType(contentType != null ? contentType : DEFAULT_CONTENT_TYPE)
                .build();
        getStorage().create(blobInfo, content);

        return new GcsObject(safeName, storedFileName, objectName, buildFileUrl(objectName));
    }

    /** best-effort 삭제. 실패해도 예외를 던지지 않는다. */
    public void delete(String objectName) {
        if (objectName == null || objectName.isBlank()) {
            return;
        }
        try {
            getStorage().delete(BlobId.of(bucket(), objectName));
        } catch (Exception e) {
            log.warn("GCS 객체 삭제에 실패했습니다. objectName={}", objectName, e);
        }
    }

    private Storage getStorage() throws IOException {
        if (storage == null) {
            synchronized (this) {
                if (storage == null) {
                    storage = StorageOptions.newBuilder()
                            .setProjectId(properties.getProjectId())
                            .setCredentials(loadCredentials())
                            .build()
                            .getService();
                }
            }
        }
        return storage;
    }

    private GoogleCredentials loadCredentials() throws IOException {
        Resource resource = resourceLoader.getResource(properties.getCredentials().getLocation());
        try (InputStream inputStream = resource.getInputStream()) {
            return GoogleCredentials.fromStream(inputStream);
        }
    }

    private String bucket() {
        return properties.getStorage().getBucket();
    }

    private String buildObjectName(String prefix, String storedFileName) {
        String normalized = normalizePrefix(prefix);
        if (normalized.isBlank()) {
            return storedFileName;
        }
        return normalized + "/" + storedFileName;
    }

    private String normalizePrefix(String prefix) {
        if (prefix == null) {
            return "";
        }
        return prefix.replaceAll("^/+", "").replaceAll("/+$", "");
    }

    private String buildFileUrl(String objectName) {
        return STORAGE_PUBLIC_URL + "/" + bucket() + "/" + objectName;
    }

    private String sanitizeFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isBlank()) {
            return "unnamed";
        }
        return originalFileName.replace("\\", "_").replace("/", "_");
    }
}
