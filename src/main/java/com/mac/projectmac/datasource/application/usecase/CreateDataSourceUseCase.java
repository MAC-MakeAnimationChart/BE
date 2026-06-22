package com.mac.projectmac.datasource.application.usecase;

import com.mac.projectmac.datasource.application.command.CreateDataSourceCommand;
import com.mac.projectmac.datasource.domain.model.DataSource;

public interface CreateDataSourceUseCase {

    DataSource create(CreateDataSourceCommand command);
}
