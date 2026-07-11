package com.mac.projectmac.problem.dataset.application.port;

import com.mac.projectmac.problem.dataset.domain.model.StoredDatasetFile;

import java.io.IOException;

public interface StoreProblemDatasetFilePort {

    StoredDatasetFile store(String originalFileName, String contentType, byte[] content, long fileSize)
            throws IOException;

    void delete(String objectPath);
}
