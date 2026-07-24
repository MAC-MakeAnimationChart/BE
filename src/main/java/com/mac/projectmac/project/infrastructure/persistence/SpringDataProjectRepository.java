package com.mac.projectmac.project.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataProjectRepository extends JpaRepository<ProjectJpaEntity, Long> {

    List<ProjectJpaEntity> findByUserId(Long userId);

    List<ProjectJpaEntity> findByFolderId(Long folderId);
}
