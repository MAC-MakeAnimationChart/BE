package com.mac.projectmac.datasource.application.command;

public record UploadDataSourceCommand(
        Long userId,
        Long projectId,
        String originalFileName,
        String contentType,
        byte[] content,
        long fileSize
) {
}
