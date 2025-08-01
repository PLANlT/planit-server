package com.planit.planit.auth.service;

public interface BlacklistTokenRedisService {
    void blacklistAccessToken(String accessToken, long ttlSeconds);
    boolean isBlacklisted(String accessToken);
    
    // Redis 데이터 정리 및 모니터링 메서드들
    void clearAllBlacklistedTokens();
    
    long getBlacklistedTokenCount();
} 