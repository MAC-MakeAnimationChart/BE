package com.mac.projectmac.auth.application.service;

import com.mac.projectmac.auth.domain.entity.User;
import com.mac.projectmac.auth.domain.exception.AuthErrorCode;
import com.mac.projectmac.auth.domain.repository.TokenRepository;
import com.mac.projectmac.auth.infrastructure.persistence.UserJpaRepository;
import com.mac.projectmac.auth.infrastructure.security.JwtTokenProvider;
import com.mac.projectmac.auth.presentation.api.request.LoginRequest;
import com.mac.projectmac.auth.presentation.api.request.RegisterRequest;
import com.mac.projectmac.auth.presentation.api.response.CheckResponse;
import com.mac.projectmac.auth.presentation.api.response.LoginResponse;
import com.mac.projectmac.auth.presentation.api.response.RegisterResponse;
import com.mac.projectmac.auth.presentation.api.response.TokenResponse;
import com.mac.projectmac.global.domain.common.error.exception.ConflictException;
import com.mac.projectmac.global.domain.common.error.exception.ForbiddenException;
import com.mac.projectmac.global.domain.common.error.exception.NotFoundException;
import com.mac.projectmac.global.domain.common.error.exception.UnauthorizedException;
import com.mac.projectmac.global.domain.common.error.exception.ValidationException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final long REFRESH_EXPIRE_SECONDS = 7 * 24 * 60 * 60L;

    private final UserJpaRepository userJpaRepository;
    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public RegisterResponse register(RegisterRequest request) {
        if (userJpaRepository.existsByEmail(request.getEmail()))
            throw new ConflictException(AuthErrorCode.USER_EMAIL_DUPLICATED);
        if (userJpaRepository.existsByNickname(request.getNickname()))
            throw new ConflictException(AuthErrorCode.USER_NICKNAME_DUPLICATED);

        User user = User.builder()
                .loginId(request.getLoginId())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .nickname(request.getNickname())
                .build();

        User saved = userJpaRepository.save(user);
        log.info("[AuthService] registered - userId: {}", saved.getUserId());
        return RegisterResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public CheckResponse check(String type, String value) {
        boolean isDuplicated = switch (type) {
            case "email"    -> userJpaRepository.existsByEmail(value);
            case "nickname" -> userJpaRepository.existsByNickname(value);
            default         -> throw new ValidationException(AuthErrorCode.INVALID_CHECK_TYPE);
        };
        return CheckResponse.from(isDuplicated);
    }

    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        User user = userJpaRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException(AuthErrorCode.USER_NOT_FOUND));

        if (user.isBanned())
            throw new ForbiddenException(AuthErrorCode.USER_ACCOUNT_BANNED);
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new UnauthorizedException(AuthErrorCode.AUTH_PASSWORD_MISMATCH);

        String accessToken  = jwtTokenProvider.generateAccessToken(user.getUserId(), user.getRole().name());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());

        tokenRepository.saveRefreshToken(user.getUserId(), refreshToken, REFRESH_EXPIRE_SECONDS);
        setRefreshTokenCookie(response, refreshToken);

        log.info("[AuthService] login - userId: {}", user.getUserId());
        return LoginResponse.from(accessToken);
    }

    public void logout(String accessToken, HttpServletResponse response) {
        Long userId = jwtTokenProvider.getUserId(accessToken);
        tokenRepository.deleteRefreshToken(userId);
        tokenRepository.addToBlacklist(accessToken, jwtTokenProvider.getRemainingSeconds(accessToken));
        deleteRefreshTokenCookie(response);
        log.info("[AuthService] logout - userId: {}", userId);
    }

    public TokenResponse reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);

        if (!jwtTokenProvider.validateToken(refreshToken))
            throw new UnauthorizedException(AuthErrorCode.AUTH_TOKEN_EXPIRED);

        Long userId = jwtTokenProvider.getUserId(refreshToken);
        String storedToken = tokenRepository.findRefreshToken(userId)
                .orElseThrow(() -> new UnauthorizedException(AuthErrorCode.AUTH_REFRESH_NOT_FOUND));

        if (!storedToken.equals(refreshToken)) {
            tokenRepository.deleteAllTokensByUserId(userId);
            throw new UnauthorizedException(AuthErrorCode.AUTH_REFRESH_MISMATCH);
        }

        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(AuthErrorCode.USER_NOT_FOUND));

        tokenRepository.deleteRefreshToken(userId);

        String newAccessToken  = jwtTokenProvider.generateAccessToken(userId, user.getRole().name());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);
        tokenRepository.saveRefreshToken(userId, newRefreshToken, REFRESH_EXPIRE_SECONDS);
        setRefreshTokenCookie(response, newRefreshToken);

        log.info("[AuthService] reissue - userId: {}", userId);
        return TokenResponse.from(newAccessToken);
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) REFRESH_EXPIRE_SECONDS);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    private void deleteRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null)
            throw new UnauthorizedException(AuthErrorCode.AUTH_REFRESH_NOT_FOUND);
        return Arrays.stream(request.getCookies())
                .filter(c -> "refreshToken".equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new UnauthorizedException(AuthErrorCode.AUTH_REFRESH_NOT_FOUND));
    }
}
