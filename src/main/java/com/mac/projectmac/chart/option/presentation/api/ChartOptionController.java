package com.mac.projectmac.chart.option.presentation.api;

import com.mac.projectmac.chart.option.application.usecase.GetChartOptionUseCase;
import com.mac.projectmac.chart.option.application.usecase.RegisterChartOptionUseCase;
import com.mac.projectmac.chart.option.application.usecase.UpdateChartOptionUseCase;
import com.mac.projectmac.chart.option.presentation.api.request.RegisterChartOptionRequest;
import com.mac.projectmac.chart.option.presentation.api.request.UpdateChartOptionRequest;
import com.mac.projectmac.chart.option.presentation.api.response.ChartOptionDetailResponse;
import com.mac.projectmac.chart.option.presentation.api.response.ChartOptionResponse;
import com.mac.projectmac.chart.option.presentation.api.response.ChartOptionSaveResponse;
import com.mac.projectmac.global.api.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects/{projectId}/chart-option")
public class ChartOptionController {

    private static final Logger log = LoggerFactory.getLogger(ChartOptionController.class);

    private final RegisterChartOptionUseCase registerChartOptionUseCase;
    private final GetChartOptionUseCase getChartOptionUseCase;
    private final UpdateChartOptionUseCase updateChartOptionUseCase;

    // 프로젝트 차트 옵션 조회 HTTP 요청을 조회 유스케이스로 전달한다.
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getChartOption(@PathVariable Long projectId) {
        log.info("[ChartOptionController] get chart option - projectId: {}", projectId);

        return ResponseEntity.ok(ApiResponse.success(
                ChartOptionResponseCode.OK,
                ChartOptionResponseMessage.OK,
                ChartOptionDetailResponse.from(getChartOptionUseCase.getByProjectId(projectId))
        ));
    }

    // 프로젝트 차트 옵션 저장 HTTP 요청을 저장 유스케이스로 전달한다.
    @PutMapping
    public ResponseEntity<ApiResponse<?>> updateChartOption(
            @PathVariable Long projectId,
            @RequestBody UpdateChartOptionRequest request
    ) {
        log.info("[ChartOptionController] update chart option - projectId: {}", projectId);

        return ResponseEntity.ok(ApiResponse.success(
                ChartOptionResponseCode.SAVED,
                ChartOptionResponseMessage.SAVED,
                ChartOptionSaveResponse.from(updateChartOptionUseCase.update(request.toCommand(projectId)))
        ));
    }

    // 차트 옵션 최초 생성 HTTP 요청을 등록 유스케이스로 전달한다.
    @PostMapping
    public ResponseEntity<ApiResponse<?>> registerChartOption(
            @PathVariable Long projectId,
            @RequestBody RegisterChartOptionRequest request
    ) {
        log.info("[ChartOptionController] register chart option - projectId: {}", projectId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        ChartOptionResponseCode.CREATED,
                        ChartOptionResponseMessage.CREATED,
                        ChartOptionResponse.from(registerChartOptionUseCase.register(request.toCommand(projectId)))
                ));
    }
}
