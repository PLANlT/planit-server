package com.planit.planit.auth.service;

import com.planit.planit.common.aop.LogExecutionTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RefreshTokenRedisServiceImpl implements RefreshTokenRedisService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    // Redis Hash 키 상수
    private static final String REFRESH_TOKEN_TO_MEMBER_ID_HASH_KEY = "refreshTokenToMemberId";
    private static final String MEMBER_ID_TO_REFRESH_TOKEN_HASH_KEY = "memberIdToRefreshToken";
    private static final Duration TTL_DURATION = Duration.ofDays(30);

    public RefreshTokenRedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @LogExecutionTime
    @Override
    public void saveRefreshToken(Long memberId, String refreshToken) {
        try {
            // refreshToken -> memberId 매핑 저장
            redisTemplate.opsForHash().put(REFRESH_TOKEN_TO_MEMBER_ID_HASH_KEY, refreshToken, memberId);
            
            // memberId -> refreshToken 매핑 저장
            redisTemplate.opsForHash().put(MEMBER_ID_TO_REFRESH_TOKEN_HASH_KEY, memberId.toString(), refreshToken);
            
            // TTL 설정 (30일)
            redisTemplate.expire(REFRESH_TOKEN_TO_MEMBER_ID_HASH_KEY, TTL_DURATION.toSeconds(), TimeUnit.SECONDS);
            redisTemplate.expire(MEMBER_ID_TO_REFRESH_TOKEN_HASH_KEY, TTL_DURATION.toSeconds(), TimeUnit.SECONDS);
            
            log.info("✅ 리프레시 토큰 저장 성공 - 회원ID: {}, 토큰: {}", memberId, refreshToken);
        } catch (Exception e) {
            log.error("❌ 리프레시 토큰 저장 실패 - 회원ID: {}, 토큰: {}", memberId, refreshToken, e);
            throw e;
        }
    }

    @LogExecutionTime
    @Override
    public void deleteByRefreshToken(String refreshToken) {
        try {
            // refreshToken으로 memberId 조회
            Object memberIdObj = redisTemplate.opsForHash().get(REFRESH_TOKEN_TO_MEMBER_ID_HASH_KEY, refreshToken);
            
            if (memberIdObj != null) {
                Long memberId = Long.valueOf(memberIdObj.toString());
                
                // 양방향 매핑 삭제
                redisTemplate.opsForHash().delete(REFRESH_TOKEN_TO_MEMBER_ID_HASH_KEY, refreshToken);
                redisTemplate.opsForHash().delete(MEMBER_ID_TO_REFRESH_TOKEN_HASH_KEY, memberId.toString());
                
                log.info("✅ 리프레시 토큰 매핑 삭제 완료 - 회원ID: {}, 토큰: {}", memberId, refreshToken);
            } else {
                log.warn("⚠️ 삭제할 리프레시 토큰을 찾을 수 없음 - 토큰: {}", refreshToken);
            }
        } catch (Exception e) {
            log.error("❌ 리프레시 토큰 삭제 실패 - 토큰: {}", refreshToken, e);
            throw e;
        }
    }

    @LogExecutionTime
    @Override
    public void deleteByMemberId(Long memberId) {
        try {
            // memberId로 refreshToken 조회
            Object refreshTokenObj = redisTemplate.opsForHash().get(MEMBER_ID_TO_REFRESH_TOKEN_HASH_KEY, memberId.toString());
            
            if (refreshTokenObj != null) {
                String refreshToken = refreshTokenObj.toString();
                
                // 양방향 매핑 삭제
                redisTemplate.opsForHash().delete(MEMBER_ID_TO_REFRESH_TOKEN_HASH_KEY, memberId.toString());
                redisTemplate.opsForHash().delete(REFRESH_TOKEN_TO_MEMBER_ID_HASH_KEY, refreshToken);
                
                log.info("✅ 회원ID 매핑 삭제 완료 - 회원ID: {}, 토큰: {}", memberId, refreshToken);
            } else {
                log.warn("⚠️ 삭제할 회원ID를 찾을 수 없음 - 회원ID: {}", memberId);
            }
        } catch (Exception e) {
            log.error("❌ 회원ID 매핑 삭제 실패 - 회원ID: {}", memberId, e);
            throw e;
        }
    }

    @LogExecutionTime
    @Override
    public Long getMemberIdByRefreshToken(String refreshToken) {
        try {
            Object memberIdObj = redisTemplate.opsForHash().get(REFRESH_TOKEN_TO_MEMBER_ID_HASH_KEY, refreshToken);
            Long memberId = memberIdObj != null ? Long.valueOf(memberIdObj.toString()) : null;
            
            log.debug("🔍 리프레시 토큰으로 회원ID 조회 - 토큰: {}, 회원ID: {}", refreshToken, memberId);
            return memberId;
        } catch (Exception e) {
            log.error("❌ 리프레시 토큰으로 회원ID 조회 실패 - 토큰: {}", refreshToken, e);
            throw e;
        }
    }

    @LogExecutionTime
    @Override
    public String getRefreshTokenByMemberId(Long memberId) {
        try {
            Object refreshTokenObj = redisTemplate.opsForHash().get(MEMBER_ID_TO_REFRESH_TOKEN_HASH_KEY, memberId.toString());
            String refreshToken = refreshTokenObj != null ? refreshTokenObj.toString() : null;
            
            log.debug("🔍 회원ID로 리프레시 토큰 조회 - 회원ID: {}, 토큰: {}", memberId, refreshToken);
            return refreshToken;
        } catch (Exception e) {
            log.error("❌ 회원ID로 리프레시 토큰 조회 실패 - 회원ID: {}", memberId, e);
            throw e;
        }
    }
    
    @LogExecutionTime
    @Override
    public void clearAllRefreshTokens() {
        try {
            redisTemplate.delete(REFRESH_TOKEN_TO_MEMBER_ID_HASH_KEY);
            redisTemplate.delete(MEMBER_ID_TO_REFRESH_TOKEN_HASH_KEY);
            log.info("✅ 모든 리프레시 토큰 삭제 완료");
        } catch (Exception e) {
            log.error("❌ 모든 리프레시 토큰 삭제 실패", e);
            throw e;
        }
    }
    
    @LogExecutionTime
    @Override
    public long getRefreshTokenCount() {
        try {
            long count = redisTemplate.opsForHash().size(REFRESH_TOKEN_TO_MEMBER_ID_HASH_KEY);
            log.info("📊 현재 리프레시 토큰 개수: {}", count);
            return count;
        } catch (Exception e) {
            log.error("❌ 리프레시 토큰 개수 조회 실패", e);
            throw e;
        }
    }
    
    @LogExecutionTime
    @Override
    public boolean validateRefreshTokenMapping(Long memberId, String refreshToken) {
        try {
            String storedToken = getRefreshTokenByMemberId(memberId);
            Long storedMemberId = getMemberIdByRefreshToken(refreshToken);
            
            boolean isValid = refreshToken.equals(storedToken) && memberId.equals(storedMemberId);
            log.debug("🔍 리프레시 토큰 매핑 유효성 검사 - 회원ID: {}, 토큰: {}, 유효성: {}", 
                       memberId, refreshToken, isValid);
            return isValid;
        } catch (Exception e) {
            log.error("❌ 리프레시 토큰 매핑 유효성 검사 실패 - 회원ID: {}, 토큰: {}", memberId, refreshToken, e);
            return false;
        }
    }
}