package com.mac.projectmac.project.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataFolderRepository extends JpaRepository<FolderJpaEntity, Long> {

    Optional<FolderJpaEntity> findByIdAndDeletedAtIsNull(Long id);

    List<FolderJpaEntity> findByUserIdAndDeletedAtIsNull(Long userId);

    List<FolderJpaEntity> findByParentIdAndDeletedAtIsNull(Long parentId);
}
