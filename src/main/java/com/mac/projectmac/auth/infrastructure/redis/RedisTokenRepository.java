package com.mac.projectmac.auth.infrastructure.redis;

import com.mac.projectmac.auth.business.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisTokenRepository implements TokenRepository {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String REFRESH_KEY   = "refresh:";
    private static final String BLACKLIST_KEY = "blacklist:";

    @Override
    public void saveRefreshToken(Long userId, String tokenValue, long expirationSeconds) {
        redisTemplate.opsForValue().set(
                REFRESH_KEY + userId, tokenValue, expirationSeconds, TimeUnit.SECONDS);
    }

    @Override
    public Optional<String> findRefreshToken(Long userId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(REFRESH_KEY + userId));
    }

    @Override
    public void deleteRefreshToken(Long userId) {
        redisTemplate.delete(REFRESH_KEY + userId);
    }

    @Override
    public void addToBlacklist(String accessToken, long remainingSeconds) {
        if (remainingSeconds > 0) {
            redisTemplate.opsForValue().set(
                    BLACKLIST_KEY + accessToken, "logout", remainingSeconds, TimeUnit.SECONDS);
        }
    }

    @Override
    public boolean isBlacklisted(String accessToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_KEY + accessToken));
    }

    @Override
    public void deleteAllTokensByUserId(Long userId) {
        redisTemplate.delete(REFRESH_KEY + userId);
    }
}
