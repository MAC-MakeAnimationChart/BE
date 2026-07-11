package com.mac.projectmac.datasource.application.service;

import com.mac.projectmac.datasource.application.usecase.GetDataSourceUseCase;
import com.mac.projectmac.datasource.domain.exception.DataSourceErrorCode;
import com.mac.projectmac.datasource.domain.model.DataSource;
import com.mac.projectmac.datasource.domain.repository.DataSourceRepository;
import com.mac.projectmac.global.domain.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DataSourceQueryService implements GetDataSourceUseCase {

    private final DataSourceRepository dataSourceRepository;

    @Override
    public DataSource getById(Long dataSourceId) {
        return dataSourceRepository.findActiveById(dataSourceId)
                .orElseThrow(() -> new NotFoundException(DataSourceErrorCode.NOT_FOUND));
    }

    @Override
    public List<DataSource> getAll() {
        return dataSourceRepository.findAllActive();
    }
}
