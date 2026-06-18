package com.mac.projectmac.datasource.api.dto;

import com.mac.projectmac.datasource.domain.model.DataSource;

import java.time.Instant;

public record CreateDataSourceResponse(
        Long sourceId,
        String sourceType,
        String fileName,
        String filePath,
        long fileSize,
        String mimeType,
        String status,
        Instant createdAt
) {
    public static CreateDataSourceResponse from(DataSource dataSource) {
        return new CreateDataSourceResponse(
                dataSource.getId(),
                dataSource.getSourceType().name(),
                dataSource.getFileName(),
                dataSource.getFilePath(),
                dataSource.getFileSize(),
                dataSource.getMimeType(),
                dataSource.getStatus().name(),
                dataSource.getCreatedAt()
        );
    }
}
