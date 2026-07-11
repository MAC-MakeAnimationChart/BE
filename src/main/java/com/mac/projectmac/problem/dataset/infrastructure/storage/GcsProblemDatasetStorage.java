package com.mac.projectmac.problem.dataset.infrastructure.storage;

import com.mac.projectmac.global.infrastructure.storage.GcsClient;
import com.mac.projectmac.global.infrastructure.storage.GcsObject;
import com.mac.projectmac.problem.dataset.application.port.StoreProblemDatasetFilePort;
import com.mac.projectmac.problem.dataset.domain.model.StoredDatasetFile;

import java.io.IOException;

public class GcsProblemDatasetStorage implements StoreProblemDatasetFilePort {

    private final GcsClient gcsClient;
    private final String prefix;

    public GcsProblemDatasetStorage(GcsClient gcsClient, String prefix) {
        this.gcsClient = gcsClient;
        this.prefix = prefix;
    }

    @Override
    public StoredDatasetFile store(String originalFileName, String contentType, byte[] content, long fileSize)
            throws IOException {
        GcsObject stored = gcsClient.upload(prefix, originalFileName, contentType, content);
        return new StoredDatasetFile(
                stored.safeName(),
                stored.storedFileName(),
                stored.fileUrl(),
                stored.objectName(),
                fileSize,
                contentType
        );
    }

    @Override
    public void delete(String objectPath) {
        gcsClient.delete(objectPath);
    }
}
