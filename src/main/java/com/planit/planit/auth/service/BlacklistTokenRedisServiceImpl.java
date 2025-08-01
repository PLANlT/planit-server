package com.planit.planit.auth.service;

import com.planit.planit.common.aop.LogExecutionTime;
import com.planit.planit.auth.entity.BlacklistTokenRedisEntity;
import com.planit.planit.auth.repository.BlacklistTokenRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
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
        try {
            BlacklistTokenRedisEntity entity = new BlacklistTokenRedisEntity(accessToken);
            blacklistTokenRedisRepository.save(entity);
            redisTemplate.expire("blacklist:" + accessToken, ttlSeconds, TimeUnit.SECONDS);
            log.info("Access token blacklisted successfully - Token: {}, TTL: {} seconds", accessToken, ttlSeconds);
        } catch (Exception e) {
            log.error("Failed to blacklist access token - Token: {}", accessToken, e);
            throw e;
        }
    }

    @LogExecutionTime
    @Override
    public boolean isBlacklisted(String accessToken) {
        try {
            boolean isBlacklisted = blacklistTokenRedisRepository.existsById(accessToken);
            log.debug("Checked blacklist status - Token: {}, IsBlacklisted: {}", accessToken, isBlacklisted);
            return isBlacklisted;
        } catch (Exception e) {
            log.error("Failed to check blacklist status - Token: {}", accessToken, e);
            throw e;
        }
    }
    
    @LogExecutionTime
    @Override
    public void clearAllBlacklistedTokens() {
        try {
            blacklistTokenRedisRepository.deleteAll();
            log.info("All blacklisted tokens cleared successfully");
        } catch (Exception e) {
            log.error("Failed to clear all blacklisted tokens", e);
            throw e;
        }
    }
    
    @LogExecutionTime
    @Override
    public long getBlacklistedTokenCount() {
        try {
            long count = blacklistTokenRedisRepository.count();
            log.info("Current blacklisted token count: {}", count);
            return count;
        } catch (Exception e) {
            log.error("Failed to get blacklisted token count", e);
            throw e;
        }
    }
} 