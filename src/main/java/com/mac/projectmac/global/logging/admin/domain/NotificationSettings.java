package com.mac.projectmac.global.logging.admin.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_settings")
@Getter
@NoArgsConstructor
public class NotificationSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "discord_webhook_url")
    private String discordWebhookUrl;

    @Column(name = "slack_webhook_url")
    private String slackWebhookUrl;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "notify_levels", nullable = false)
    private String notifyLevels = "ERROR";

    public void update(String discordWebhookUrl, String slackWebhookUrl, boolean enabled, String notifyLevels) {
        this.discordWebhookUrl = discordWebhookUrl;
        this.slackWebhookUrl = slackWebhookUrl;
        this.enabled = enabled;
        this.notifyLevels = notifyLevels;
    }
}
