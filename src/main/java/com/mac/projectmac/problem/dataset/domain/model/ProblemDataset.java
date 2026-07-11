package com.mac.projectmac.problem.dataset.domain.model;

import lombok.Getter;

import java.time.Instant;

@Getter
public class ProblemDataset {

    private Long id;
    private Long ownerId;
    private String fileName;
    private String storedFileName;
    private String fileUrl;
    private String filePath;
    private long fileSize;
    private String mimeType;
    private DatasetStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private ProblemDataset(
            Long id,
            Long ownerId,
            String fileName,
            String storedFileName,
            String fileUrl,
            String filePath,
            long fileSize,
            String mimeType,
            DatasetStatus status,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {
        this.id = id;
        this.ownerId = ownerId;
        this.fileName = fileName;
        this.storedFileName = storedFileName;
        this.fileUrl = fileUrl;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    /** 업로드된 파일 정보로 활성(ACTIVE) 데이터셋을 생성한다. */
    public static ProblemDataset createActive(Long ownerId, StoredDatasetFile stored) {
        return new ProblemDataset(
                null,
                ownerId,
                stored.originalFileName(),
                stored.storedFileName(),
                stored.fileUrl(),
                stored.objectPath(),
                stored.fileSize(),
                stored.contentType(),
                DatasetStatus.ACTIVE,
                null,
                null,
                null
        );
    }

    /** 영속성 계층에서 조회한 값을 데이터셋 도메인 객체로 복원한다. */
    public static ProblemDataset restore(
            Long id,
            Long ownerId,
            String fileName,
            String storedFileName,
            String fileUrl,
            String filePath,
            long fileSize,
            String mimeType,
            DatasetStatus status,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {
        return new ProblemDataset(
                id,
                ownerId,
                fileName,
                storedFileName,
                fileUrl,
                filePath,
                fileSize,
                mimeType,
                status,
                createdAt,
                updatedAt,
                deletedAt
        );
    }

    /** 소프트 삭제: 상태를 DELETED 로 바꾸고 삭제 시각을 기록한다. */
    public void softDelete(Instant deletedAt) {
        this.status = DatasetStatus.DELETED;
        this.deletedAt = deletedAt;
    }

    public boolean isDeleted() {
        return status == DatasetStatus.DELETED;
    }
}
