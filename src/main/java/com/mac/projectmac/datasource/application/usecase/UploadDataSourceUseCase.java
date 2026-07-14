package com.mac.projectmac.datasource.application.usecase;

import com.mac.projectmac.datasource.application.command.UploadDataSourceCommand;
import com.mac.projectmac.datasource.domain.model.DataSource;

public interface UploadDataSourceUseCase {

    DataSource upload(UploadDataSourceCommand command);
}
