package com.mac.projectmac.datasource.infrastructure.persistence;

import com.mac.projectmac.datasource.domain.model.DataSource;
import com.mac.projectmac.datasource.domain.repository.DataSourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DataSourceRepositoryAdapter implements DataSourceRepository {

    private final SpringDataDataSourceRepository springDataDataSourceRepository;

    @Override
    public DataSource save(DataSource dataSource) {
        return springDataDataSourceRepository.save(DataSourceJpaEntity.from(dataSource)).toDomain();
    }

    @Override
    public Optional<DataSource> findByProjectId(Long projectId) {
        return springDataDataSourceRepository.findByProjectId(projectId)
                .map(DataSourceJpaEntity::toDomain);
    }

    @Override
    public boolean existsByProjectId(Long projectId) {
        return springDataDataSourceRepository.existsByProjectId(projectId);
    }

    @Override
    public void deleteByProjectId(Long projectId) {
        springDataDataSourceRepository.deleteByProjectId(projectId);
    }
}
