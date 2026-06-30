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
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    // private static final String CODE_REGISTER = "AUT-201";
    // private static final String CODE_OK       = "AUT-200";

    private final AuthService authService;

    @Operation(summary = "회원가입")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "AUT-002: 이메일 중복"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "AUT-003: 닉네임 중복")
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        log.info("[AuthController] register - email: {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        AuthResponseCode.CREATED,
                        AuthResponseMessage.REGISTER,
                        RegisterResponse.from(authService.register(request.toCommand()))));
    }

    @Operation(summary = "이메일/닉네임 중복 확인")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "중복 확인 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "AUT-010: 잘못된 type 파라미터")
    })
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<CheckResponse>> check(
            @RequestParam String type, @RequestParam String value) {
        log.info("[AuthController] check - type: {}, value: {}", type, value);
        return ResponseEntity.ok(ApiResponse.success(
                AuthResponseCode.OK,
                AuthResponseMessage.CHECK,
                CheckResponse.from(authService.check(type, value))));
    }

    @Operation(summary = "로그인")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "AUT-001: 유저 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "AUT-006: 비밀번호 불일치"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "AUT-004: 정지된 계정")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        log.info("[AuthController] login - email: {}", request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(
                AuthResponseCode.OK,
                AuthResponseMessage.LOGIN,
                LoginResponse.from(authService.login(request.toCommand(), response))));
    }

    @Operation(summary = "로그아웃")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공")
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String bearer, HttpServletResponse response) {
        authService.logout(bearer.replace("Bearer ", ""), response);
        log.info("[AuthController] logout");
        return ResponseEntity.ok(ApiResponse.success(AuthResponseCode.OK, AuthResponseMessage.LOGOUT));
    }

    @Operation(summary = "토큰 재발급")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "재발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "AUT-007: 토큰 만료"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "AUT-008: Refresh Token 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "AUT-009: Refresh Token 불일치 (탈취 감지)")
    })
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(
            HttpServletRequest request, HttpServletResponse response) {
        log.info("[AuthController] reissue");
        return ResponseEntity.ok(ApiResponse.success(
                AuthResponseCode.OK,
                AuthResponseMessage.REISSUE,
                TokenResponse.from(authService.reissue(request, response))));
    }
}
