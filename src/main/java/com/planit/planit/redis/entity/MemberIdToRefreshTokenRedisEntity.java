package com.planit.planit.redis.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "memberIdToRefreshToken", timeToLive = 1209600)
public class MemberIdToRefreshTokenRedisEntity {
    @Id
    private Long memberId;
    private String refreshToken;

    public MemberIdToRefreshTokenRedisEntity() {}
    public MemberIdToRefreshTokenRedisEntity(Long memberId, String refreshToken) {
        this.memberId = memberId;
        this.refreshToken = refreshToken;
    }
    public Long getMemberId() { return memberId; }
    public String getRefreshToken() { return refreshToken; }
} 