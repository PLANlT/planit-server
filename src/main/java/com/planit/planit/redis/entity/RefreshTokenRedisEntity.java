package com.planit.planit.redis.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "refresh", timeToLive = 1209600) // 2주(초 단위)
public class RefreshTokenRedisEntity {
    @Id
    private String refreshToken;
    private Long memberId;

    public RefreshTokenRedisEntity() {}
    public RefreshTokenRedisEntity(String refreshToken, Long memberId) {
        this.refreshToken = refreshToken;
        this.memberId = memberId;
    }
    public String getRefreshToken() { return refreshToken; }
    public Long getMemberId() { return memberId; }
} 