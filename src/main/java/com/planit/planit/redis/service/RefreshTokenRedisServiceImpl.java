package com.planit.planit.redis.service;

import com.planit.planit.redis.entity.RefreshTokenToMemberIdRedisEntity;
import com.planit.planit.redis.entity.MemberIdToRefreshTokenRedisEntity;
import com.planit.planit.redis.repository.RefreshTokenToMemberIdRedisRepository;
import com.planit.planit.redis.repository.MemberIdToRefreshTokenRedisRepository;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenRedisServiceImpl implements  RefreshTokenRedisService {
    private final RefreshTokenToMemberIdRedisRepository refreshTokenToMemberIdRedisRepository;
    private final MemberIdToRefreshTokenRedisRepository memberIdToRefreshTokenRedisRepository;

    public RefreshTokenRedisServiceImpl(RefreshTokenToMemberIdRedisRepository refreshTokenToMemberIdRedisRepository,
                                        MemberIdToRefreshTokenRedisRepository memberIdToRefreshTokenRedisRepository) {
        this.refreshTokenToMemberIdRedisRepository = refreshTokenToMemberIdRedisRepository;
        this.memberIdToRefreshTokenRedisRepository = memberIdToRefreshTokenRedisRepository;
    }

    @Override
    public void saveRefreshToken(Long memberId, String refreshToken) {
        refreshTokenToMemberIdRedisRepository.save(new RefreshTokenToMemberIdRedisEntity(refreshToken, memberId));
        memberIdToRefreshTokenRedisRepository.save(new MemberIdToRefreshTokenRedisEntity(memberId, refreshToken));
    }

    @Override
    public void deleteByRefreshToken(String refreshToken) {
        RefreshTokenToMemberIdRedisEntity entity = refreshTokenToMemberIdRedisRepository.findById(refreshToken).orElse(null);
        if (entity != null) {
            memberIdToRefreshTokenRedisRepository.deleteById(entity.getMemberId());
        }
        refreshTokenToMemberIdRedisRepository.deleteById(refreshToken);
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        MemberIdToRefreshTokenRedisEntity entity = memberIdToRefreshTokenRedisRepository.findById(memberId).orElse(null);
        if (entity != null) {
            refreshTokenToMemberIdRedisRepository.deleteById(entity.getRefreshToken());
        }
        memberIdToRefreshTokenRedisRepository.deleteById(memberId);
    }

    @Override
    public Long getMemberIdByRefreshToken(String refreshToken) {
        return refreshTokenToMemberIdRedisRepository.findById(refreshToken)
                .map(RefreshTokenToMemberIdRedisEntity::getMemberId)
                .orElse(null);
    }

    @Override
    public String getRefreshTokenByMemberId(Long memberId) {
        return memberIdToRefreshTokenRedisRepository.findById(memberId)
                .map(MemberIdToRefreshTokenRedisEntity::getRefreshToken)
                .orElse(null);
    }
}