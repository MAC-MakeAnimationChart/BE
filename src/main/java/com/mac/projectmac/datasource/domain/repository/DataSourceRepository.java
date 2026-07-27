package com.mac.projectmac.datasource.domain.repository;

import com.mac.projectmac.datasource.domain.model.DataSource;

import java.util.Optional;

public interface DataSourceRepository {

    // 데이터소스 도메인 객체를 저장(신규/교체)한다.
    DataSource save(DataSource dataSource);

    // 프로젝트의 데이터소스를 조회한다 (1:1).
    Optional<DataSource> findByProjectId(Long projectId);

    // 프로젝트에 데이터소스가 존재하는지 확인한다.
    boolean existsByProjectId(Long projectId);

    // 프로젝트의 데이터소스를 삭제한다 (하드 삭제).
    void deleteByProjectId(Long projectId);
}
