package com.mac.projectmac.admin.presentation.api;

import com.mac.projectmac.admin.application.service.AdminService;
import com.mac.projectmac.admin.presentation.api.request.AccountStatusRequest;
import com.mac.projectmac.admin.presentation.api.response.AccountStatusResponse;
import com.mac.projectmac.global.api.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "관리자 API")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private static final String CODE_OK = "ADM-200";

    private final AdminService adminService;

    @Operation(summary = "계정 상태 변경")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상태 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "AUT-001: 유저 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "접근 권한 없음 (ADMIN만 허용)")
    })
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<AccountStatusResponse>> changeStatus(
            @PathVariable Long userId,
            @Valid @RequestBody AccountStatusRequest request) {
        log.info("[AdminController] changeStatus - userId: {}, status: {}", userId, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success(
                AdminResponseCode.OK,
                AdminResponseMessage.CHANGE_STATUS,
                adminService.changeStatus(userId, request)));
    }
}
