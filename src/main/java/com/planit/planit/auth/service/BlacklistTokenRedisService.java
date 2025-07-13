package com.planit.planit.auth.service;

public interface BlacklistTokenRedisService {
    void blacklistAccessToken(String accessToken, long ttlSeconds);
    boolean isBlacklisted(String accessToken);
} 