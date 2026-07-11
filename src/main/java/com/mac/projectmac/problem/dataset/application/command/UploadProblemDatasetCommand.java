package com.mac.projectmac.problem.dataset.application.command;

public record UploadProblemDatasetCommand(
        Long ownerId,
        String originalFileName,
        String contentType,
        byte[] content,
        long fileSize
) {
}
