package com.mac.projectmac.global.logging.presentation.api;

import com.mac.projectmac.global.api.common.ApiResponse;
import com.mac.projectmac.global.logging.application.service.NotificationService;
import com.mac.projectmac.global.logging.presentation.api.dto.NotificationSettingsRequest;
import com.mac.projectmac.global.logging.presentation.api.dto.NotificationSettingsResponse;
import com.mac.projectmac.global.logging.presentation.api.dto.TestNotificationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/notifications")
@RequiredArgsConstructor
@Tag(name = "Admin - Notifications", description = "관리자 알림 설정 API (ADMIN 권한 필요)")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "알림 설정 변경 (Discord/Slack 웹훅, 알림 레벨)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "접근 권한 없음 (ADMIN만 허용)")
    })
    @PutMapping("/settings")
    public ResponseEntity<ApiResponse<NotificationSettingsResponse>> updateSettings(
            @RequestBody NotificationSettingsRequest request) {
        NotificationSettingsResponse result = notificationService.updateSettings(request);
        return ResponseEntity.ok(ApiResponse.success(LogResponseCode.OK, LogResponseMessage.NOTIFICATION_UPDATED, result));
    }

    @Operation(summary = "테스트 알림 전송")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "전송 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "접근 권한 없음 (ADMIN만 허용)")
    })
    @PostMapping("/test")
    public ResponseEntity<ApiResponse<Void>> sendTestNotification(
            @RequestBody TestNotificationRequest request) {
        notificationService.sendTestNotification(request.getChannel());
        return ResponseEntity.ok(ApiResponse.success(LogResponseCode.OK, LogResponseMessage.NOTIFICATION_SENT, null));
    }
}
