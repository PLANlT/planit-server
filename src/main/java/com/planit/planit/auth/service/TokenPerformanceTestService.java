package com.planit.planit.auth.service;

import com.planit.planit.common.aop.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class TokenPerformanceTestService {

    private final RedisTemplate<String, Object> redisTemplate;

    @LogExecutionTime
    public void fetchWithKey(String refreshToken) {
        redisTemplate.opsForValue().get("token:" + refreshToken);
    }

    @LogExecutionTime
    public void fetchWithLinearSearch(String refreshToken) {
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys) {
            String value = redisTemplate.opsForValue().get(key).toString();
            if (refreshToken.equals(value)) {
                break;
            }
        }
    }

}
