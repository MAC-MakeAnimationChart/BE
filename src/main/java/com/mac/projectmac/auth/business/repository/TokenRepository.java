package com.mac.projectmac.auth.domain.repository;

import java.util.Optional;

public interface TokenRepository {
    void saveRefreshToken(Long userId, String tokenValue, long expirationSeconds);
    Optional<String> findRefreshToken(Long userId);
    void deleteRefreshToken(Long userId);
    void addToBlacklist(String accessToken, long remainingSeconds);
    boolean isBlacklisted(String accessToken);
    void deleteAllTokensByUserId(Long userId);
}
