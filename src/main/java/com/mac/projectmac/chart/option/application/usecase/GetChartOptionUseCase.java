package com.mac.projectmac.chart.option.application.usecase;

import com.mac.projectmac.chart.option.domain.model.ChartOption;

public interface GetChartOptionUseCase {

    // 인증 사용자가 접근 가능한 프로젝트의 저장된 차트 옵션을 조회한다.
    ChartOption getByProjectId(Long userId, Long projectId);
}
