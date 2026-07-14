package com.mac.projectmac.datasource.infrastructure.persistence;

import com.mac.projectmac.datasource.domain.model.SourceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataDataSourceRepository extends JpaRepository<DataSourceJpaEntity, Long> {

    Optional<DataSourceJpaEntity> findByIdAndStatus(Long id, SourceStatus status);

    List<DataSourceJpaEntity> findAllByStatusOrderByIdDesc(SourceStatus status);
}
