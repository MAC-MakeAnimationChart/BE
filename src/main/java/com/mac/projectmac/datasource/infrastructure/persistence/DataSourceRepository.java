package com.mac.projectmac.datasource.infrastructure.persistence;

import com.mac.projectmac.datasource.domain.model.DataSource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataSourceRepository extends JpaRepository<DataSource, Long> {
}
