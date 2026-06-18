package com.mac.projectmac.chart.option.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataChartOptionRepository extends JpaRepository<ChartOptionJpaEntity, Long> {

    // 프로젝트에 삭제되지 않은 차트 옵션이 존재하는지 확인한다.
    boolean existsByProjectIdAndDeletedAtIsNull(Long projectId);

    // 프로젝트 번호로 삭제되지 않은 차트 옵션 엔티티를 조회한다.
    Optional<ChartOptionJpaEntity> findByProjectIdAndDeletedAtIsNull(Long projectId);
}
