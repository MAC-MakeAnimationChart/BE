package com.mac.projectmac.datasource.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "data_sources")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DataSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ownerId;

    private Long projectId;

    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    private String fileName;

    @Column(length = 1000)
    private String filePath;

    private long fileSize;

    private String mimeType;

    @Enumerated(EnumType.STRING)
    private SourceStatus status;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    private Instant deletedAt;

    private DataSource(
            Long ownerId,
            Long projectId,
            SourceType sourceType,
            String fileName,
            String filePath,
            long fileSize,
            String mimeType,
            SourceStatus status
    ) {
        this.ownerId = ownerId;
        this.projectId = projectId;
        this.sourceType = sourceType;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.status = status;
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
                ownerId,
                projectId,
                sourceType,
                fileName,
                filePath,
                fileSize,
                mimeType,
                SourceStatus.PENDING
        );
    }

    public void softDelete(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }
}
