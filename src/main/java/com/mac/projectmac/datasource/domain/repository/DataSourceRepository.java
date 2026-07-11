package com.mac.projectmac.datasource.domain.repository;

import com.mac.projectmac.datasource.domain.model.DataSource;

import java.util.List;
import java.util.Optional;

public interface DataSourceRepository {

    // 데이터소스 도메인 객체를 저장(신규/변경)한다.
    DataSource save(DataSource dataset);

    // 활성(ACTIVE) 데이터소스를 id 로 조회한다.
    Optional<DataSource> findActiveById(Long id);

    // 활성(ACTIVE) 데이터소스를 최신순으로 조회한다.
    List<DataSource> findAllActive();
}
