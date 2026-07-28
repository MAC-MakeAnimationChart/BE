package com.mac.projectmac.datasource.domain.model;

import lombok.Getter;

import java.time.Instant;

@Getter
public class DataSource {

    private Long id;
    private Long projectId;
    private String fileName;
    private String storedFileName;
    private String fileUrl;
    private String filePath;
    private long fileSize;
    private String mimeType;
    private Instant createdAt;
    private Instant updatedAt;

    private DataSource(
            Long id,
            Long projectId,
            String fileName,
            String storedFileName,
            String fileUrl,
            String filePath,
            long fileSize,
            String mimeType,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.projectId = projectId;
        this.fileName = fileName;
        this.storedFileName = storedFileName;
        this.fileUrl = fileUrl;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** 업로드된 파일 정보로 프로젝트에 종속된 데이터소스를 생성한다. */
    public static DataSource create(Long projectId, StoredFile stored) {
        return new DataSource(
                null,
                projectId,
                stored.originalFileName(),
                stored.storedFileName(),
                stored.fileUrl(),
                stored.objectPath(),
                stored.fileSize(),
                stored.contentType(),
                null,
                null
        );
    }

    /** 영속성 계층에서 조회한 값을 데이터소스 도메인 객체로 복원한다. */
    public static DataSource restore(
            Long id,
            Long projectId,
            String fileName,
            String storedFileName,
            String fileUrl,
            String filePath,
            long fileSize,
            String mimeType,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new DataSource(
                id,
                projectId,
                fileName,
                storedFileName,
                fileUrl,
                filePath,
                fileSize,
                mimeType,
                createdAt,
                updatedAt
        );
    }

    /** 같은 프로젝트의 데이터소스를 새 파일로 교체한다 (id·projectId·createdAt 유지). */
    public void replaceFile(StoredFile stored) {
        this.fileName = stored.originalFileName();
        this.storedFileName = stored.storedFileName();
        this.fileUrl = stored.fileUrl();
        this.filePath = stored.objectPath();
        this.fileSize = stored.fileSize();
        this.mimeType = stored.contentType();
    }
}
