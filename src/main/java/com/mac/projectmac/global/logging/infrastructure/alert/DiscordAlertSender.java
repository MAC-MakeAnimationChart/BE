package com.mac.projectmac.global.logging.infrastructure.alert;

import com.mac.projectmac.global.logging.application.port.AlertSender;
import com.mac.projectmac.global.logging.config.AlertProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordAlertSender implements AlertSender {

    private final AlertProperties alertProperties;
    private final RestTemplate restTemplate;

    @Override
    public void send(AlertPayload payload) {
        String webhookUrl = alertProperties.getDiscord().getWebhookUrl();
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.warn("[DiscordAlertSender] Discord Webhook URL 미설정 - 알림 생략");
            return;
        }
        try {
            String content = buildMessage(payload);
            restTemplate.postForEntity(webhookUrl, Map.of("content", content), String.class);
            log.info("[DiscordAlertSender] Discord 알림 발송 완료 - exceptionClass={}", payload.getExceptionClass());
        } catch (Exception e) {
            log.error("[DiscordAlertSender] Discord 알림 발송 실패 - webhookUrl={}", webhookUrl, e);
        }
    }

    @Override
    public boolean supports(AlertChannel channel) {
        return channel == AlertChannel.DISCORD || channel == AlertChannel.ALL;
    }

    private String buildMessage(AlertPayload payload) {
        return String.format("""
                🚨 **서버 에러 발생**
                - 발생 시각: %s
                - 요청: %s %s
                - 유저: %s
                - 예외: %s
                - 메시지: %s
                ```
                %s
                ```
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
