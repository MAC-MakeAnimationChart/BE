package com.mac.projectmac.global.logging.presentation.api;

import com.mac.projectmac.global.api.common.ApiResponse;
import com.mac.projectmac.global.logging.application.service.NotificationService;
import com.mac.projectmac.global.logging.presentation.api.dto.NotificationSettingsRequest;
import com.mac.projectmac.global.logging.presentation.api.dto.NotificationSettingsResponse;
import com.mac.projectmac.global.logging.presentation.api.dto.TestNotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PutMapping("/settings")
    public ResponseEntity<ApiResponse<NotificationSettingsResponse>> updateSettings(
            @RequestBody NotificationSettingsRequest request) {
        NotificationSettingsResponse result = notificationService.updateSettings(request);
        return ResponseEntity.ok(ApiResponse.success(LogResponseCode.OK, LogResponseMessage.NOTIFICATION_UPDATED, result));
    }

    @PostMapping("/test")
    public ResponseEntity<ApiResponse<Void>> sendTestNotification(
            @RequestBody TestNotificationRequest request) {
        notificationService.sendTestNotification(request.getChannel());
        return ResponseEntity.ok(ApiResponse.success(LogResponseCode.OK, LogResponseMessage.NOTIFICATION_SENT, null));
    }
}
