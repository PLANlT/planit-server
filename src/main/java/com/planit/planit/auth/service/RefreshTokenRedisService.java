package com.planit.planit.auth.service;

public interface RefreshTokenRedisService {
    void saveRefreshToken(Long memberId, String refreshToken);

    void deleteByRefreshToken(String refreshToken);

    void deleteByMemberId(Long memberId);

    Long getMemberIdByRefreshToken(String refreshToken);

    String getRefreshTokenByMemberId(Long memberId);
}
