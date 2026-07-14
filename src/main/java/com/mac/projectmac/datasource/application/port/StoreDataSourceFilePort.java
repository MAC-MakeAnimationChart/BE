package com.mac.projectmac.datasource.application.port;

import com.mac.projectmac.datasource.domain.model.StoredFile;

import java.io.IOException;

public interface StoreDataSourceFilePort {

    StoredFile store(String originalFileName, String contentType, byte[] content, long fileSize)
            throws IOException;

    void delete(String objectPath);
}
