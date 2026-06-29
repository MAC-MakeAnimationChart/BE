package com.mac.projectmac.global.logging.application.service;

import com.mac.projectmac.global.domain.common.error.exception.NotFoundException;
import com.mac.projectmac.global.logging.domain.error.LogErrorCode;
import com.mac.projectmac.global.logging.infrastructure.entity.AppLog;
import com.mac.projectmac.global.logging.infrastructure.entity.SlowQueryLog;
import com.mac.projectmac.global.logging.infrastructure.repository.AppLogRepository;
import com.mac.projectmac.global.logging.infrastructure.repository.SlowQueryLogRepository;
import com.mac.projectmac.global.logging.presentation.api.dto.AppLogResponse;
import com.mac.projectmac.global.logging.presentation.api.dto.SlowQueryLogResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LogAdminService {

    private static final int DEFAULT_DAYS = 7;
    private static final int MAX_PAGE_SIZE = 100;

    private final AppLogRepository appLogRepository;
    private final SlowQueryLogRepository slowQueryLogRepository;

    public Page<AppLogResponse> getLogs(String level, Instant startDate, Instant endDate,
                                        Long userId, String requestId, String keyword,
                                        int page, int size) {
        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size);

        List<AppLog.LogLevel> levels = resolveLevels(level);
        Instant start = startDate != null ? startDate : Instant.now().minus(DEFAULT_DAYS, ChronoUnit.DAYS);
        Instant end = endDate != null ? endDate : Instant.now();

        log.info("[LogAdminService] 로그 목록 조회 - level={}, startDate={}, endDate={}", level, start, end);

        return appLogRepository.search(levels, start, end, userId, requestId, keyword, pageable)
                .map(log -> new AppLogResponse(log, false));
    }

    public AppLogResponse getLog(Long logId) {
        AppLog appLog = appLogRepository.findById(logId)
                .orElseThrow(() -> new NotFoundException(LogErrorCode.LOG_NOT_FOUND));

        log.info("[LogAdminService] 로그 단건 조회 - logId={}", logId);
        boolean includeStackTrace = appLog.getLevel() == AppLog.LogLevel.ERROR;
        return new AppLogResponse(appLog, includeStackTrace);
    }

    public Page<SlowQueryLogResponse> getSlowQueries(int thresholdMs, Instant startDate,
                                                      Instant endDate, int page, int size) {
        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size);

        Instant start = startDate != null ? startDate : Instant.now().minus(DEFAULT_DAYS, ChronoUnit.DAYS);
        Instant end = endDate != null ? endDate : Instant.now();

        log.info("[LogAdminService] 슬로우 쿼리 조회 - thresholdMs={}", thresholdMs);

        return slowQueryLogRepository.search(thresholdMs, start, end, pageable)
                .map(SlowQueryLogResponse::new);
    }

    private List<AppLog.LogLevel> resolveLevels(String level) {
        if (level == null || level.isBlank()) {
            return List.of(AppLog.LogLevel.ERROR, AppLog.LogLevel.WARN);
        }
        return List.of(AppLog.LogLevel.valueOf(level.toUpperCase()));
    }
}
