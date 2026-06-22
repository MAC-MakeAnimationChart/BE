package com.mac.projectmac.datasource.domain.model;

import lombok.Getter;

import java.time.Instant;

@Getter
public class DataSource {

    private Long id;
    private Long ownerId;
    private Long projectId;
    private SourceType sourceType;
    private String fileName;
    private String filePath;
    private long fileSize;
    private String mimeType;
    private SourceStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private DataSource(
            Long id,
            Long ownerId,
            Long projectId,
            SourceType sourceType,
            String fileName,
            String filePath,
            long fileSize,
            String mimeType,
            SourceStatus status,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {
        this.id = id;
        this.ownerId = ownerId;
        this.projectId = projectId;
        this.sourceType = sourceType;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    /** 업로드 직후 파싱 대기(PENDING) 상태로 생성한다. */
    public static DataSource createPending(
            Long ownerId,
            Long projectId,
            SourceType sourceType,
            String fileName,
            String filePath,
            long fileSize,
            String mimeType
    ) {
        return new DataSource(
                null,
                ownerId,
                projectId,
                sourceType,
                fileName,
                filePath,
                fileSize,
                mimeType,
                SourceStatus.PENDING,
                null,
                null,
                null
        );
    }

    /** 영속성 계층에서 조회한 값을 데이터소스 도메인 객체로 복원한다. */
    public static DataSource restore(
            Long id,
            Long ownerId,
            Long projectId,
            SourceType sourceType,
            String fileName,
            String filePath,
            long fileSize,
            String mimeType,
            SourceStatus status,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {
        return new DataSource(
                id,
                ownerId,
                projectId,
                sourceType,
                fileName,
                filePath,
                fileSize,
                mimeType,
                status,
                createdAt,
                updatedAt,
                deletedAt
        );
    }

    public void softDelete(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }
}
