package com.mac.projectmac.datasource.infrastructure.persistence;

import com.mac.projectmac.datasource.domain.model.DataSource;
import com.mac.projectmac.datasource.domain.model.SourceStatus;
import com.mac.projectmac.datasource.domain.model.SourceType;
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
public class DataSourceJpaEntity {

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

    private DataSourceJpaEntity(
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

    // 데이터소스 도메인 객체를 JPA 엔티티로 변환한다.
    public static DataSourceJpaEntity from(DataSource dataSource) {
        return new DataSourceJpaEntity(
                dataSource.getId(),
                dataSource.getOwnerId(),
                dataSource.getProjectId(),
                dataSource.getSourceType(),
                dataSource.getFileName(),
                dataSource.getFilePath(),
                dataSource.getFileSize(),
                dataSource.getMimeType(),
                dataSource.getStatus(),
                dataSource.getCreatedAt(),
                dataSource.getUpdatedAt(),
                dataSource.getDeletedAt()
        );
    }

    // JPA 엔티티를 데이터소스 도메인 객체로 복원한다.
    public DataSource toDomain() {
        return DataSource.restore(
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
}
