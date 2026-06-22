package com.mac.projectmac.datasource.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataDataSourceRepository extends JpaRepository<DataSourceJpaEntity, Long> {
}
