package com.mac.projectmac.problem.dataset.infrastructure.storage;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.mac.projectmac.problem.dataset.application.port.StoreProblemDatasetFilePort;
import com.mac.projectmac.problem.dataset.domain.model.StoredDatasetFile;
import com.mac.projectmac.global.infrastructure.storage.GcpStorageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
public class GcsProblemDatasetStorage implements StoreProblemDatasetFilePort {

    private static final String STORAGE_PUBLIC_URL = "https://storage.googleapis.com";
    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private final GcpStorageProperties properties;
    private final ResourceLoader resourceLoader;
    private volatile Storage storage;

    public GcsProblemDatasetStorage(GcpStorageProperties properties, ResourceLoader resourceLoader) {
        this.properties = properties;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public StoredDatasetFile store(String originalFileName, String contentType, byte[] content, long fileSize)
            throws IOException {
        String safeName = sanitizeFileName(originalFileName);
        String storedFileName = UUID.randomUUID() + "_" + safeName;
        String objectName = buildObjectName(storedFileName);

        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucket(), objectName))
                .setContentType(contentType != null ? contentType : DEFAULT_CONTENT_TYPE)
                .build();

        getStorage().create(blobInfo, content);

        return new StoredDatasetFile(
                safeName,
                storedFileName,
                buildFileUrl(objectName),
                objectName,
                fileSize,
                contentType
        );
    }

    @Override
    public void delete(String objectPath) {
        if (objectPath == null || objectPath.isBlank()) {
            return;
        }
        try {
            getStorage().delete(BlobId.of(bucket(), objectPath));
        } catch (Exception e) {
            log.warn("GCS 데이터셋 파일 삭제에 실패했습니다. objectPath={}", objectPath, e);
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

    private String buildObjectName(String storedFileName) {
        String prefix = normalizePrefix(properties.getStorage().getProblemDatasetPrefix());
        if (prefix.isBlank()) {
            return storedFileName;
        }
        return prefix + "/" + storedFileName;
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
