package com.mac.projectmac.datasource.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataDataSourceRepository extends JpaRepository<DataSourceJpaEntity, Long> {

    Optional<DataSourceJpaEntity> findByProjectId(Long projectId);

    boolean existsByProjectId(Long projectId);

    void deleteByProjectId(Long projectId);
}
