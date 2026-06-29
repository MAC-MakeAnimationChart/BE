package com.mac.projectmac.auth.presentation.api;

import com.mac.projectmac.auth.application.service.AuthService;
import com.mac.projectmac.auth.presentation.api.request.LoginRequest;
import com.mac.projectmac.auth.presentation.api.request.RegisterRequest;
import com.mac.projectmac.auth.presentation.api.response.CheckResponse;
import com.mac.projectmac.auth.presentation.api.response.LoginResponse;
import com.mac.projectmac.auth.presentation.api.response.RegisterResponse;
import com.mac.projectmac.auth.presentation.api.response.TokenResponse;
import com.mac.projectmac.global.api.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private static final String CODE_REGISTER = "AUT-201";
    private static final String CODE_OK       = "AUT-200";

    private final AuthService authService;

    @Operation(summary = "회원가입")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        log.info("[AuthController] register - email: {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(CODE_REGISTER, "회원가입 성공", authService.register(request)));
    }

    @Operation(summary = "이메일/닉네임 중복 확인")
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<CheckResponse>> check(
            @RequestParam String type, @RequestParam String value) {
        log.info("[AuthController] check - type: {}, value: {}", type, value);
        return ResponseEntity.ok(ApiResponse.success(CODE_OK, "중복 확인 성공", authService.check(type, value)));
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        log.info("[AuthController] login - email: {}", request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(CODE_OK, "로그인 성공", authService.login(request, response)));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String bearer, HttpServletResponse response) {
        authService.logout(bearer.replace("Bearer ", ""), response);
        log.info("[AuthController] logout");
        return ResponseEntity.ok(ApiResponse.success(CODE_OK, "로그아웃 성공"));
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(
            HttpServletRequest request, HttpServletResponse response) {
        log.info("[AuthController] reissue");
        return ResponseEntity.ok(ApiResponse.success(CODE_OK, "토큰 재발급 성공", authService.reissue(request, response)));
    }
}
