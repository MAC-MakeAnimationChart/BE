package com.mac.projectmac.datasource.infrastructure.persistence;

import com.mac.projectmac.datasource.domain.model.SourceStatus;
import com.mac.projectmac.datasource.domain.model.DataSource;
import com.mac.projectmac.datasource.domain.repository.DataSourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DataSourceRepositoryAdapter implements DataSourceRepository {

    private final SpringDataDataSourceRepository springDataDataSourceRepository;

    @Override
    public DataSource save(DataSource dataset) {
        return springDataDataSourceRepository.save(DataSourceJpaEntity.from(dataset)).toDomain();
    }

    @Override
    public Optional<DataSource> findActiveById(Long id) {
        return springDataDataSourceRepository.findByIdAndStatus(id, SourceStatus.ACTIVE)
                .map(DataSourceJpaEntity::toDomain);
    }

    @Override
    public List<DataSource> findAllActive() {
        return springDataDataSourceRepository.findAllByStatusOrderByIdDesc(SourceStatus.ACTIVE)
                .stream()
                .map(DataSourceJpaEntity::toDomain)
                .toList();
    }
}
