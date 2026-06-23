package com.mac.projectmac.global.logging.presentation.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class NotificationSettingsRequest {

    private String discordWebhookUrl;
    private String slackWebhookUrl;
    private boolean enabled;
    private List<String> notifyLevels;
}
