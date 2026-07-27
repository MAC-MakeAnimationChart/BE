package com.mac.projectmac.datasource.infrastructure.persistence;

import com.mac.projectmac.datasource.domain.model.DataSource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(
        name = "data_sources",
        uniqueConstraints = @UniqueConstraint(name = "uk_data_sources_project", columnNames = "project_id")
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DataSourceJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    private String fileName;

    private String storedFileName;

    @Column(length = 1000)
    private String fileUrl;

    @Column(length = 1000)
    private String filePath;

    private long fileSize;

    private String mimeType;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    private DataSourceJpaEntity(
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

    // 데이터소스 도메인 객체를 JPA 엔티티로 변환한다.
    public static DataSourceJpaEntity from(DataSource dataSource) {
        return new DataSourceJpaEntity(
                dataSource.getId(),
                dataSource.getProjectId(),
                dataSource.getFileName(),
                dataSource.getStoredFileName(),
                dataSource.getFileUrl(),
                dataSource.getFilePath(),
                dataSource.getFileSize(),
                dataSource.getMimeType(),
                dataSource.getCreatedAt(),
                dataSource.getUpdatedAt()
        );
    }

    // JPA 엔티티를 데이터소스 도메인 객체로 복원한다.
    public DataSource toDomain() {
        return DataSource.restore(
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
}
