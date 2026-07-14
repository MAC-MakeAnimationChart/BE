package com.mac.projectmac.datasource.application.command;

public record UploadDataSourceCommand(
        Long ownerId,
        String originalFileName,
        String contentType,
        byte[] content,
        long fileSize
) {
}
