package com.planit.planit.redis.service;

public interface BlacklistTokenRedisService {
    void blacklistAccessToken(String accessToken, long ttlSeconds);
    boolean isBlacklisted(String accessToken);
} 