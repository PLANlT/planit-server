package com.planit.planit.config.redis;

import com.planit.planit.redis.entity.RefreshTokenRedisEntity;
import com.planit.planit.redis.repository.RefreshTokenRedisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@DataRedisTest
class RefreshTokenRedisRepositoryTest {

    @Autowired
    private RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    @DisplayName("refresh:{refreshToken} 키로 memberId 저장/조회 성공")
    void saveAndFindById() {
        // given
        String refreshToken = "test-refresh-token-123";
        Long memberId = 42L;
        RefreshTokenRedisEntity entity = new RefreshTokenRedisEntity(refreshToken, memberId);

        // when
        refreshTokenRedisRepository.save(entity);
        RefreshTokenRedisEntity found = refreshTokenRedisRepository.findById(refreshToken).orElse(null);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(found.getMemberId()).isEqualTo(memberId);
    }
} 