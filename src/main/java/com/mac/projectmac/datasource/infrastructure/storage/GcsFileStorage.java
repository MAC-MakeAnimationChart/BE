package com.mac.projectmac.datasource.infrastructure.storage;

import com.mac.projectmac.datasource.application.port.StoreFilePort;
import com.mac.projectmac.datasource.domain.model.StoredFile;
import com.mac.projectmac.global.infrastructure.storage.GcsClient;
import com.mac.projectmac.global.infrastructure.storage.GcsObject;

import java.io.IOException;

public class GcsFileStorage implements StoreFilePort {

    private final GcsClient gcsClient;
    private final String prefix;

    public GcsFileStorage(GcsClient gcsClient, String prefix) {
        this.gcsClient = gcsClient;
        this.prefix = prefix;
    }

    @Override
    public StoredFile store(String originalFileName, String contentType, byte[] content, long fileSize)
            throws IOException {
        GcsObject stored = gcsClient.upload(prefix, originalFileName, contentType, content);
        return new StoredFile(
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
