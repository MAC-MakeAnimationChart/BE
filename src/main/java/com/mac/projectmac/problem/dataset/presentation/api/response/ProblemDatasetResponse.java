package com.mac.projectmac.problem.dataset.presentation.api.response;

import com.mac.projectmac.problem.dataset.domain.model.ProblemDataset;

import java.time.Instant;

public record ProblemDatasetResponse(
        Long datasetId,
        String fileName,
        String fileUrl,
        long fileSize,
        String mimeType,
        String status,
        Instant createdAt
) {
    public static ProblemDatasetResponse from(ProblemDataset dataset) {
        return new ProblemDatasetResponse(
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
