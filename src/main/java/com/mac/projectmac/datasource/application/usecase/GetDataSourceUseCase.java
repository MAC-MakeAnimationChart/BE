package com.mac.projectmac.datasource.application.usecase;

import com.mac.projectmac.datasource.domain.model.DataSource;

import java.util.List;

public interface GetDataSourceUseCase {

    DataSource getById(Long dataSourceId);

    List<DataSource> getAll();
}
