package com.mac.projectmac.global.logging.admin.controller;

import com.mac.projectmac.global.api.common.ApiResponse;
import com.mac.projectmac.global.logging.admin.dto.NotificationSettingsRequest;
import com.mac.projectmac.global.logging.admin.dto.NotificationSettingsResponse;
import com.mac.projectmac.global.logging.admin.dto.TestNotificationRequest;
import com.mac.projectmac.global.logging.admin.service.NotificationService;
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
        return ResponseEntity.ok(ApiResponse.success("NOTIFICATION-SUCCESS", "알림 설정이 업데이트되었습니다.", result));
    }

    @PostMapping("/test")
    public ResponseEntity<ApiResponse<Void>> sendTestNotification(
            @RequestBody TestNotificationRequest request) {
        notificationService.sendTestNotification(request.getChannel());
        return ResponseEntity.ok(ApiResponse.success("NOTIFICATION-SUCCESS", "테스트 알림이 발송되었습니다.", null));
    }
}
