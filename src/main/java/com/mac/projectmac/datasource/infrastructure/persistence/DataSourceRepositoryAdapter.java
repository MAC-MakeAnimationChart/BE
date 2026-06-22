package com.mac.projectmac.datasource.infrastructure.persistence;

import com.mac.projectmac.datasource.domain.model.DataSource;
import com.mac.projectmac.datasource.domain.repository.DataSourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DataSourceRepositoryAdapter implements DataSourceRepository {

    private final SpringDataDataSourceRepository springDataDataSourceRepository;

    // 데이터소스 도메인 객체를 JPA 엔티티로 변환해 저장한 뒤 도메인 객체로 복원한다.
    @Override
    public DataSource save(DataSource dataSource) {
        return springDataDataSourceRepository.save(DataSourceJpaEntity.from(dataSource)).toDomain();
    }
}
