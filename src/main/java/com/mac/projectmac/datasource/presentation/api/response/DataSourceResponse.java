package com.mac.projectmac.datasource.presentation.api.response;

import com.mac.projectmac.datasource.domain.model.DataSource;

import java.time.Instant;

public record DataSourceResponse(
        Long dataSourceId,
        Long projectId,
        String fileName,
        String fileUrl,
        long fileSize,
        String mimeType,
        Instant createdAt
) {
    public static DataSourceResponse from(DataSource dataSource) {
        return new DataSourceResponse(
                dataSource.getId(),
                dataSource.getProjectId(),
                dataSource.getFileName(),
                dataSource.getFileUrl(),
                dataSource.getFileSize(),
                dataSource.getMimeType(),
                dataSource.getCreatedAt()
        );
    }
}
