package com.planit.planit.auth.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "refreshTokenToMemberId", timeToLive = 2592000)
public class RefreshTokenToMemberIdRedisEntity {
    @Id
    private String refreshToken;
    private Long memberId;

    public RefreshTokenToMemberIdRedisEntity() {}
    public RefreshTokenToMemberIdRedisEntity(String refreshToken, Long memberId) {
        this.refreshToken = refreshToken;
        this.memberId = memberId;
    }
    public String getRefreshToken() { return refreshToken; }
    public Long getMemberId() { return memberId; }
} 