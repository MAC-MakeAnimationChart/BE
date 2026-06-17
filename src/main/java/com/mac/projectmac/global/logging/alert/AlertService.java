package com.mac.projectmac.global.logging.alert;

import com.mac.projectmac.global.logging.config.AlertProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {

    private final List<AlertSender> alertSenders;
    private final AlertProperties alertProperties;

    // Throttling: 예외 클래스별 마지막 발송 시각
    private final ConcurrentHashMap<String, Instant> lastSentMap = new ConcurrentHashMap<>();

    @Async("alertExecutor")
    public void sendAlert(Exception exception, HttpServletRequest request) {
        // MDC 복사 (비동기 스레드에 전달)
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        if (mdcContext != null) {
            MDC.setContextMap(mdcContext);
        }

        try {
            if (!alertProperties.isEnabled()) return;
            if (isThrottled(exception)) return;

            AlertPayload payload = buildPayload(exception, request);
            alertSenders.stream()
                    .filter(sender -> sender.supports(AlertChannel.ALL))
                    .forEach(sender -> {
                        try {
                            sender.send(payload);
                        } catch (Exception e) {
                            log.error("[AlertService] 알림 발송 실패 - sender={}", sender.getClass().getSimpleName(), e);
                        }
                    });

            log.info("[AlertService] 알림 발송 완료 - exceptionClass={}", exception.getClass().getSimpleName());
        } finally {
            MDC.clear();
        }
    }

    @Async("alertExecutor")
    public void sendTestAlert(AlertChannel channel) {
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        if (mdcContext != null) {
            MDC.setContextMap(mdcContext);
        }

        try {
            AlertPayload payload = AlertPayload.builder()
                    .occurredAt(Instant.now())
                    .requestMethod("TEST")
                    .requestUri("/test")
                    .userId("system")
                    .exceptionClass("TestAlert")
                    .message("테스트 알림입니다.")
                    .stackTrace("N/A")
                    .build();

            alertSenders.stream()
                    .filter(sender -> sender.supports(channel))
                    .forEach(sender -> sender.send(payload));

            log.info("[AlertService] 테스트 알림 발송 완료 - channel={}", channel);
        } finally {
            MDC.clear();
        }
    }

    private boolean isThrottled(Exception exception) {
        String exceptionClass = exception.getClass().getName();
        Instant now = Instant.now();
        Instant lastSent = lastSentMap.get(exceptionClass);

        if (lastSent != null &&
                now.isBefore(lastSent.plusSeconds(alertProperties.getThrottleMinutes() * 60L))) {
            log.debug("[AlertService] 알림 throttle 적용 - exceptionClass={}", exceptionClass);
            return true;
        }

        lastSentMap.put(exceptionClass, now);
        return false;
    }

    private AlertPayload buildPayload(Exception exception, HttpServletRequest request) {
        String stackTrace = Arrays.stream(exception.getStackTrace())
                .limit(5)
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));

        return AlertPayload.builder()
                .occurredAt(Instant.now())
                .requestMethod(request.getMethod())
                .requestUri(request.getRequestURI())
                .userId(MDC.get("userId") != null ? MDC.get("userId") : "ANONYMOUS")
                .exceptionClass(exception.getClass().getName())
                .message(exception.getMessage())
                .stackTrace(stackTrace)
                .build();
    }
}
