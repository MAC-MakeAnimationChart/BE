package com.mac.projectmac.chart.option.infrastructure.persistence;

import com.mac.projectmac.chart.option.domain.model.ChartOption;
import com.mac.projectmac.chart.option.domain.repository.ChartOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChartOptionRepositoryAdapter implements ChartOptionRepository {

    private final SpringDataChartOptionRepository springDataChartOptionRepository;

    // 차트 옵션 도메인 객체를 JPA 엔티티로 변환해 저장한다.
    @Override
    public ChartOption save(ChartOption chartOption) {
        return springDataChartOptionRepository.save(ChartOptionJpaEntity.from(chartOption)).toDomain();
    }

    // 프로젝트에 활성 차트 옵션이 존재하는지 JPA 저장소에 위임한다.
    @Override
    public boolean existsActiveByProjectId(Long projectId) {
        return springDataChartOptionRepository.existsByProjectIdAndDeletedAtIsNull(projectId);
    }

    // 프로젝트 번호로 활성 차트 옵션을 조회한 뒤 도메인 객체로 변환한다.
    @Override
    public Optional<ChartOption> findActiveByProjectId(Long projectId) {
        return springDataChartOptionRepository.findByProjectIdAndDeletedAtIsNull(projectId)
                .map(ChartOptionJpaEntity::toDomain);
    }
}
