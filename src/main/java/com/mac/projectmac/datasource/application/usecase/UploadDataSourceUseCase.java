package com.mac.projectmac.datasource.application.usecase;

import com.mac.projectmac.datasource.application.command.UploadDataSourceCommand;
import com.mac.projectmac.datasource.domain.model.DataSource;

public interface UploadDataSourceUseCase {

    Result upload(UploadDataSourceCommand command);

    /** created=true 면 신규 생성(201), false 면 기존 데이터소스 교체(200). */
    record Result(DataSource dataSource, boolean created) {
    }
}
