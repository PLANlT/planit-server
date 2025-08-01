package com.planit.planit.auth.service;

public interface RefreshTokenRedisService {
    void saveRefreshToken(Long memberId, String refreshToken);

    void deleteByRefreshToken(String refreshToken);

    void deleteByMemberId(Long memberId);

    Long getMemberIdByRefreshToken(String refreshToken);

    String getRefreshTokenByMemberId(Long memberId);
    
    // Redis 데이터 정리 및 모니터링 메서드들
    void clearAllRefreshTokens();
    
    long getRefreshTokenCount();
    
    boolean validateRefreshTokenMapping(Long memberId, String refreshToken);
}
