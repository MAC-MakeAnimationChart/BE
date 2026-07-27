package com.mac.projectmac.datasource.application.usecase;

import com.mac.projectmac.datasource.domain.model.DataSource;

public interface GetDataSourceUseCase {

    DataSource getByProjectId(Long userId, Long projectId);
}
