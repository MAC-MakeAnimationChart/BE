package com.mac.projectmac.global.logging.alert;

import com.mac.projectmac.global.logging.config.AlertProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlackAlertSender implements AlertSender {

    private final AlertProperties alertProperties;
    private final RestTemplate restTemplate;

    @Override
    public void send(AlertPayload payload) {
        String webhookUrl = alertProperties.getSlack().getWebhookUrl();
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.warn("[SlackAlertSender] Slack Webhook URL 미설정 - 알림 생략");
            return;
        }
        try {
            String text = buildMessage(payload);
            restTemplate.postForEntity(webhookUrl, Map.of("text", text), String.class);
            log.info("[SlackAlertSender] Slack 알림 발송 완료 - exceptionClass={}", payload.getExceptionClass());
        } catch (Exception e) {
            log.error("[SlackAlertSender] Slack 알림 발송 실패 - webhookUrl={}", webhookUrl, e);
        }
    }

    @Override
    public boolean supports(AlertChannel channel) {
        return channel == AlertChannel.SLACK || channel == AlertChannel.ALL;
    }

    private String buildMessage(AlertPayload payload) {
        return String.format("""
                🚨 *서버 에러 발생*
                • 발생 시각: %s
                • 요청: %s %s
                • 유저: %s
                • 예외: %s
                • 메시지: %s
                ```%s```
                """,
                payload.getOccurredAt(),
                payload.getRequestMethod(), payload.getRequestUri(),
                payload.getUserId(),
                payload.getExceptionClass(),
                payload.getMessage(),
                payload.getStackTrace()
        );
    }
}
