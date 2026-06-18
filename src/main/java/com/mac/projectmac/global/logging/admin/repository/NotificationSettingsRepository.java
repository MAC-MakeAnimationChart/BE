package com.mac.projectmac.global.logging.admin.repository;

import com.mac.projectmac.global.logging.admin.domain.NotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {
}
