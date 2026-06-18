package com.mac.projectmac.global.logging.admin.dto;

import com.mac.projectmac.global.logging.admin.domain.NotificationSettings;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@Builder
public class NotificationSettingsResponse {

    private Long id;
    private String discordWebhookUrl;
    private String slackWebhookUrl;
    private boolean enabled;
    private List<String> notifyLevels;

    public static NotificationSettingsResponse from(NotificationSettings settings) {
        return NotificationSettingsResponse.builder()
                .id(settings.getId())
                .discordWebhookUrl(settings.getDiscordWebhookUrl())
                .slackWebhookUrl(settings.getSlackWebhookUrl())
                .enabled(settings.isEnabled())
                .notifyLevels(Arrays.asList(settings.getNotifyLevels().split(",")))
                .build();
    }
}
