package com.mac.projectmac.datasource.domain.repository;

import com.mac.projectmac.datasource.domain.model.DataSource;

public interface DataSourceRepository {

    // 데이터소스 도메인 객체를 저장한다.
    DataSource save(DataSource dataSource);
}
