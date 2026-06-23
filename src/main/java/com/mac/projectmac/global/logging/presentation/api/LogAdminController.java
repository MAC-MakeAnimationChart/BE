package com.mac.projectmac.global.logging.presentation.api;

import com.mac.projectmac.global.api.common.ApiResponse;
import com.mac.projectmac.global.logging.application.service.LogAdminService;
import com.mac.projectmac.global.logging.presentation.api.dto.AppLogResponse;
import com.mac.projectmac.global.logging.presentation.api.dto.SlowQueryLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class LogAdminController {

    private final LogAdminService logAdminService;

    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<Page<AppLogResponse>>> getLogs(
            @RequestParam(required = false) String level,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String requestId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<AppLogResponse> result = logAdminService.getLogs(
                level, startDate, endDate, userId, requestId, keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(LogResponseCode.OK, LogResponseMessage.LOGS_OK, result));
    }

    @GetMapping("/logs/{logId}")
    public ResponseEntity<ApiResponse<AppLogResponse>> getLog(@PathVariable Long logId) {
        AppLogResponse result = logAdminService.getLog(logId);
        return ResponseEntity.ok(ApiResponse.success(LogResponseCode.OK, LogResponseMessage.LOG_OK, result));
    }

    @GetMapping("/logs/slow-queries")
    public ResponseEntity<ApiResponse<Page<SlowQueryLogResponse>>> getSlowQueries(
            @RequestParam(defaultValue = "1000") int thresholdMs,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<SlowQueryLogResponse> result = logAdminService.getSlowQueries(
                thresholdMs, startDate, endDate, page, size);
        return ResponseEntity.ok(ApiResponse.success(LogResponseCode.OK, LogResponseMessage.SLOW_QUERIES_OK, result));
    }
}
