package com.mac.projectmac.chart.option.domain.repository;

import com.mac.projectmac.chart.option.domain.model.ChartOption;

import java.util.Optional;

public interface ChartOptionRepository {

    // 차트 옵션 도메인 객체를 저장한다.
    ChartOption save(ChartOption chartOption);

    // 프로젝트에 활성 차트 옵션이 존재하는지 확인한다.
    boolean existsActiveByProjectId(Long projectId);

    // 프로젝트 번호로 삭제되지 않은 차트 옵션을 조회한다.
    Optional<ChartOption> findActiveByProjectId(Long projectId);
}
