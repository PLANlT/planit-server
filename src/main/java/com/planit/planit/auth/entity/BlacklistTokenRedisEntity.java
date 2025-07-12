package com.planit.planit.auth.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "blacklist", timeToLive = 3600)
public class BlacklistTokenRedisEntity {
    @Id
    private String accessToken;

    public BlacklistTokenRedisEntity() {}
    public BlacklistTokenRedisEntity(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getAccessToken() { return accessToken; }
} 