package com.mac.projectmac.problem.dataset.infrastructure.persistence;

import com.mac.projectmac.problem.dataset.domain.model.DatasetStatus;
import com.mac.projectmac.problem.dataset.domain.model.ProblemDataset;
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
@Table(name = "problem_datasets")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProblemDatasetJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ownerId;

    private String fileName;

    private String storedFileName;

    @Column(length = 1000)
    private String fileUrl;

    @Column(length = 1000)
    private String filePath;

    private long fileSize;

    private String mimeType;

    @Enumerated(EnumType.STRING)
    private DatasetStatus status;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    private Instant deletedAt;

    private ProblemDatasetJpaEntity(
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

    // 데이터셋 도메인 객체를 JPA 엔티티로 변환한다.
    public static ProblemDatasetJpaEntity from(ProblemDataset dataset) {
        return new ProblemDatasetJpaEntity(
                dataset.getId(),
                dataset.getOwnerId(),
                dataset.getFileName(),
                dataset.getStoredFileName(),
                dataset.getFileUrl(),
                dataset.getFilePath(),
                dataset.getFileSize(),
                dataset.getMimeType(),
                dataset.getStatus(),
                dataset.getCreatedAt(),
                dataset.getUpdatedAt(),
                dataset.getDeletedAt()
        );
    }

    // JPA 엔티티를 데이터셋 도메인 객체로 복원한다.
    public ProblemDataset toDomain() {
        return ProblemDataset.restore(
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
}
