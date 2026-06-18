package com.mac.projectmac.datasource.application.command;

import com.mac.projectmac.datasource.domain.model.SourceType;

public record CreateDataSourceCommand(
        Long ownerId,
        Long projectId,
        SourceType sourceType,
        String originalFileName,
        String contentType,
        byte[] content,
        long fileSize
) {
}
