package com.mac.projectmac.global.logging.infrastructure.repository;

import com.mac.projectmac.global.logging.infrastructure.entity.NotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {
}
