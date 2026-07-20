package com.mac.projectmac.chart.option.application.service;

import com.mac.projectmac.chart.option.application.port.ProjectAccessPort;
import com.mac.projectmac.chart.option.application.usecase.GetChartOptionUseCase;
import com.mac.projectmac.chart.option.domain.exception.ChartOptionErrorCode;
import com.mac.projectmac.chart.option.domain.model.ChartOption;
import com.mac.projectmac.chart.option.domain.repository.ChartOptionRepository;
import com.mac.projectmac.global.domain.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChartOptionQueryService implements GetChartOptionUseCase {

    private static final Logger log = LoggerFactory.getLogger(ChartOptionQueryService.class);

    private final ChartOptionRepository chartOptionRepository;
    private final ProjectAccessPort projectAccessPort;

    // 프로젝트 검증 후 저장된 활성 차트 옵션을 조회한다.
    @Override
    public ChartOption getByProjectId(Long userId, Long projectId) {
        validateReadableProject(projectId, userId);

        ChartOption chartOption = chartOptionRepository.findActiveByProjectId(projectId)
                .orElseThrow(() -> new NotFoundException(ChartOptionErrorCode.CHART_OPTION_NOT_FOUND));
        log.info("[ChartOptionQueryService] found chart option - projectId: {}, chartOptionId: {}",
                projectId,
                chartOption.getChartOptionId()
        );

        return chartOption;
    }

    // 인증 사용자가 프로젝트의 차트 옵션을 조회할 수 있는지 확인한다.
    private void validateReadableProject(Long projectId, Long userId) {
        if (!projectAccessPort.canReadProject(projectId, userId)) {
            throw new NotFoundException(ChartOptionErrorCode.PROJECT_NOT_FOUND);
        }
    }
}
