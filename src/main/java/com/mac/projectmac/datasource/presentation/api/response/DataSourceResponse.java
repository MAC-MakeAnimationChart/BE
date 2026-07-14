package com.mac.projectmac.datasource.presentation.api.response;

import com.mac.projectmac.datasource.domain.model.DataSource;

import java.time.Instant;

public record DataSourceResponse(
        Long dataSourceId,
        String fileName,
        String fileUrl,
        long fileSize,
        String mimeType,
        String status,
        Instant createdAt
) {
    public static DataSourceResponse from(DataSource dataset) {
        return new DataSourceResponse(
                dataset.getId(),
                dataset.getFileName(),
                dataset.getFileUrl(),
                dataset.getFileSize(),
                dataset.getMimeType(),
                dataset.getStatus().name(),
                dataset.getCreatedAt()
        );
    }
}
