package com.planit.planit.auth.service;

import com.planit.planit.common.aop.LogExecutionTime;
import com.planit.planit.auth.entity.RefreshTokenToMemberIdRedisEntity;
import com.planit.planit.auth.entity.MemberIdToRefreshTokenRedisEntity;
import com.planit.planit.auth.repository.RefreshTokenToMemberIdRedisRepository;
import com.planit.planit.auth.repository.MemberIdToRefreshTokenRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class RefreshTokenRedisServiceImpl implements  RefreshTokenRedisService {
    
    private final RefreshTokenToMemberIdRedisRepository refreshTokenToMemberIdRedisRepository;
    private final MemberIdToRefreshTokenRedisRepository memberIdToRefreshTokenRedisRepository;

    public RefreshTokenRedisServiceImpl(RefreshTokenToMemberIdRedisRepository refreshTokenToMemberIdRedisRepository,
                                        MemberIdToRefreshTokenRedisRepository memberIdToRefreshTokenRedisRepository) {
        this.refreshTokenToMemberIdRedisRepository = refreshTokenToMemberIdRedisRepository;
        this.memberIdToRefreshTokenRedisRepository = memberIdToRefreshTokenRedisRepository;
    }

    @LogExecutionTime
    @Override
    public void saveRefreshToken(Long memberId, String refreshToken) {
        try {
            RefreshTokenToMemberIdRedisEntity refreshTokenEntity = new RefreshTokenToMemberIdRedisEntity(refreshToken, memberId);
            MemberIdToRefreshTokenRedisEntity memberIdEntity = new MemberIdToRefreshTokenRedisEntity(memberId, refreshToken);
            
            refreshTokenToMemberIdRedisRepository.save(refreshTokenEntity);
            memberIdToRefreshTokenRedisRepository.save(memberIdEntity);
            
            log.info("Refresh token saved successfully - MemberId: {}, Token: {}", memberId, refreshToken);
        } catch (Exception e) {
            log.error("Failed to save refresh token - MemberId: {}, Token: {}", memberId, refreshToken, e);
            throw e;
        }
    }

    @LogExecutionTime
    @Override
    public void deleteByRefreshToken(String refreshToken) {
        try {
            RefreshTokenToMemberIdRedisEntity entity = refreshTokenToMemberIdRedisRepository.findById(refreshToken).orElse(null);
            if (entity != null) {
                memberIdToRefreshTokenRedisRepository.deleteById(entity.getMemberId());
                log.info("Deleted refresh token mapping - MemberId: {}, Token: {}", entity.getMemberId(), refreshToken);
            }
            refreshTokenToMemberIdRedisRepository.deleteById(refreshToken);
        } catch (Exception e) {
            log.error("Failed to delete refresh token - Token: {}", refreshToken, e);
            throw e;
        }
    }

    @LogExecutionTime
    @Override
    public void deleteByMemberId(Long memberId) {
        try {
            MemberIdToRefreshTokenRedisEntity entity = memberIdToRefreshTokenRedisRepository.findById(memberId).orElse(null);
            if (entity != null) {
                refreshTokenToMemberIdRedisRepository.deleteById(entity.getRefreshToken());
                log.info("Deleted member ID mapping - MemberId: {}, Token: {}", memberId, entity.getRefreshToken());
            }
            memberIdToRefreshTokenRedisRepository.deleteById(memberId);
        } catch (Exception e) {
            log.error("Failed to delete member ID mapping - MemberId: {}", memberId, e);
            throw e;
        }
    }

    @LogExecutionTime
    @Override
    public Long getMemberIdByRefreshToken(String refreshToken) {
        try {
            Long memberId = refreshTokenToMemberIdRedisRepository.findById(refreshToken)
                    .map(RefreshTokenToMemberIdRedisEntity::getMemberId)
                    .orElse(null);
            log.debug("Retrieved member ID by refresh token - Token: {}, MemberId: {}", refreshToken, memberId);
            return memberId;
        } catch (Exception e) {
            log.error("Failed to get member ID by refresh token - Token: {}", refreshToken, e);
            throw e;
        }
    }

    @LogExecutionTime
    @Override
    public String getRefreshTokenByMemberId(Long memberId) {
        try {
            String refreshToken = memberIdToRefreshTokenRedisRepository.findById(memberId)
                    .map(MemberIdToRefreshTokenRedisEntity::getRefreshToken)
                    .orElse(null);
            log.debug("Retrieved refresh token by member ID - MemberId: {}, Token: {}", memberId, refreshToken);
            return refreshToken;
        } catch (Exception e) {
            log.error("Failed to get refresh token by member ID - MemberId: {}", memberId, e);
            throw e;
        }
    }
    
    @LogExecutionTime
    @Override
    public void clearAllRefreshTokens() {
        try {
            refreshTokenToMemberIdRedisRepository.deleteAll();
            memberIdToRefreshTokenRedisRepository.deleteAll();
            log.info("All refresh tokens cleared successfully");
        } catch (Exception e) {
            log.error("Failed to clear all refresh tokens", e);
            throw e;
        }
    }
    
    @LogExecutionTime
    @Override
    public long getRefreshTokenCount() {
        try {
            long count = refreshTokenToMemberIdRedisRepository.count();
            log.info("Current refresh token count: {}", count);
            return count;
        } catch (Exception e) {
            log.error("Failed to get refresh token count", e);
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
            log.debug("Refresh token mapping validation - MemberId: {}, Token: {}, IsValid: {}", 
                       memberId, refreshToken, isValid);
            return isValid;
        } catch (Exception e) {
            log.error("Failed to validate refresh token mapping - MemberId: {}, Token: {}", memberId, refreshToken, e);
            return false;
        }
    }
}