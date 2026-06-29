package com.mac.projectmac.global.logging.application.service;

import com.mac.projectmac.global.domain.common.error.exception.ValidationException;
import com.mac.projectmac.global.logging.domain.error.LogErrorCode;
import com.mac.projectmac.global.logging.infrastructure.alert.AlertChannel;
import com.mac.projectmac.global.logging.infrastructure.entity.NotificationSettings;
import com.mac.projectmac.global.logging.infrastructure.repository.NotificationSettingsRepository;
import com.mac.projectmac.global.logging.presentation.api.dto.NotificationSettingsRequest;
import com.mac.projectmac.global.logging.presentation.api.dto.NotificationSettingsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationSettingsRepository notificationSettingsRepository;
    private final AlertService alertService;

    private static final long SETTINGS_ID = 1L;

    @Transactional
    public NotificationSettingsResponse updateSettings(NotificationSettingsRequest request) {
        validateWebhookUrls(request);

        String notifyLevels = request.getNotifyLevels() != null
                ? String.join(",", request.getNotifyLevels())
                : "ERROR";

        NotificationSettings settings = notificationSettingsRepository.findById(SETTINGS_ID)
                .orElseGet(NotificationSettings::new);

        settings.update(
                request.getDiscordWebhookUrl(),
                request.getSlackWebhookUrl(),
                request.isEnabled(),
                notifyLevels
        );

        NotificationSettings saved = notificationSettingsRepository.save(settings);
        log.info("[NotificationService] 알림 설정 업데이트 완료 - enabled={}", saved.isEnabled());
        return NotificationSettingsResponse.from(saved);
    }

    public void sendTestNotification(String channelStr) {
        AlertChannel channel = parseChannel(channelStr);
        alertService.sendTestAlert(channel);
        log.info("[NotificationService] 테스트 알림 발송 요청 - channel={}", channel);
    }

    private void validateWebhookUrls(NotificationSettingsRequest request) {
        String discord = request.getDiscordWebhookUrl();
        String slack = request.getSlackWebhookUrl();

        if (discord != null && !discord.isEmpty() && !discord.startsWith("https://")) {
            throw new ValidationException(LogErrorCode.NOTIFICATION_INVALID_URL);
        }
        if (slack != null && !slack.isEmpty() && !slack.startsWith("https://")) {
            throw new ValidationException(LogErrorCode.NOTIFICATION_INVALID_URL);
        }
        if (request.isEnabled()
                && (discord == null || discord.isEmpty())
                && (slack == null || slack.isEmpty())) {
            throw new ValidationException(LogErrorCode.NOTIFICATION_WEBHOOK_NOT_SET);
        }
    }

    private AlertChannel parseChannel(String channelStr) {
        try {
            return AlertChannel.valueOf(channelStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(LogErrorCode.NOTIFICATION_INVALID_CHANNEL);
        }
    }
}
