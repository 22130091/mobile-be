package com.client.mobile.config.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Service
public class RedisTokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisTokenService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isBlacklisted(String tokenId) {
        if (tokenId == null || tokenId.isEmpty()) return false;
        return redisTemplate.hasKey(tokenId);
    }

    public void blacklistToken(String tokenId, long ttlSeconds) {
        if (tokenId == null || tokenId.isEmpty()) return;
        redisTemplate.opsForValue().set(tokenId, "BLACKLISTED", Duration.ofSeconds(ttlSeconds));
    }

    public void saveUserToken(String username, String tokenId, long ttlSeconds) {
        if (username == null || username.isEmpty() || tokenId == null || tokenId.isEmpty()) return;
        String key = "USER_TOKENS:" + username;
        redisTemplate.opsForSet().add(key, tokenId);
        redisTemplate.expire(key, Duration.ofSeconds(ttlSeconds));
    }

    public void removeAllTokens(String username) {
        String key = "USER_TOKENS:" + username;
        redisTemplate.delete(key);
    }

    public Set<Object> getAllTokens(String username) {
        String key = "USER_TOKENS:" + username;
        return redisTemplate.opsForSet().members(key);
    }

    public void logoutAllDevices(String username) {
        Set<Object> tokens = getAllTokens(username);
        if (tokens != null) {
            for (Object token : tokens) {
                blacklistToken(token.toString(), 3600);
            }
        }
        removeAllTokens(username);
    }
}
