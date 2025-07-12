package com.planit.planit.auth.service;

import com.planit.planit.common.aop.LogExecutionTime;
import com.planit.planit.auth.entity.BlacklistTokenRedisEntity;
import com.planit.planit.auth.repository.BlacklistTokenRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class BlacklistTokenRedisServiceImpl implements BlacklistTokenRedisService {
    private final BlacklistTokenRedisRepository blacklistTokenRedisRepository;
    private final RedisTemplate<String, Object> redisTemplate;


    @Autowired
    public BlacklistTokenRedisServiceImpl(BlacklistTokenRedisRepository blacklistTokenRedisRepository, RedisTemplate<String, Object> redisTemplate) {
        this.blacklistTokenRedisRepository = blacklistTokenRedisRepository;
        this.redisTemplate = redisTemplate;
    }

    @LogExecutionTime
    @Override
    public void blacklistAccessToken(String accessToken, long ttlSeconds) {
        BlacklistTokenRedisEntity entity = new BlacklistTokenRedisEntity(accessToken);
        blacklistTokenRedisRepository.save(entity);
        redisTemplate.expire("blacklist:" + accessToken, ttlSeconds, TimeUnit.SECONDS);
    }

    @LogExecutionTime
    @Override
    public boolean isBlacklisted(String accessToken) {
        return blacklistTokenRedisRepository.existsById(accessToken);
    }
} 