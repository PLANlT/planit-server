package com.planit.planit.auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.HashOperations;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenRedisServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    
    @Mock
    private HashOperations<String, Object, Object> hashOperations;
    
    @InjectMocks
    private RefreshTokenRedisServiceImpl refreshTokenRedisService;

    @Test
    @DisplayName("refreshToken, memberId 양방향 저장 테스트")
    void saveRefreshToken() {
        // given
        Long memberId = 1L;
        String refreshToken = "test-refresh-token";
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);

        // when
        refreshTokenRedisService.saveRefreshToken(memberId, refreshToken);

        // then
        verify(hashOperations, times(1)).put("refreshTokenToMemberId", refreshToken, memberId);
        verify(hashOperations, times(1)).put("memberIdToRefreshToken", memberId.toString(), refreshToken);
        verify(redisTemplate, times(2)).expire(anyString(), eq(30L * 24 * 60 * 60), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("refreshToken으로 삭제 테스트")
    void deleteByRefreshToken() {
        // given
        String refreshToken = "test-refresh-token";
        Long memberId = 1L;
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get("refreshTokenToMemberId", refreshToken)).thenReturn(memberId);

        // when
        refreshTokenRedisService.deleteByRefreshToken(refreshToken);

        // then
        verify(hashOperations, times(1)).delete("refreshTokenToMemberId", refreshToken);
        verify(hashOperations, times(1)).delete("memberIdToRefreshToken", memberId.toString());
    }

    @Test
    @DisplayName("refreshToken으로 삭제 테스트 - 토큰이 존재하지 않는 경우")
    void deleteByRefreshToken_NotFound() {
        // given
        String refreshToken = "non-existent-token";
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get("refreshTokenToMemberId", refreshToken)).thenReturn(null);

        // when
        refreshTokenRedisService.deleteByRefreshToken(refreshToken);

        // then
        verify(hashOperations, never()).delete(eq("refreshTokenToMemberId"), any());
        verify(hashOperations, never()).delete(eq("memberIdToRefreshToken"), any());
    }

    @Test
    @DisplayName("memberId로 삭제 테스트")
    void deleteByMemberId() {
        // given
        String refreshToken = "test-refresh-token";
        Long memberId = 1L;
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get("memberIdToRefreshToken", memberId.toString())).thenReturn(refreshToken);

        // when
        refreshTokenRedisService.deleteByMemberId(memberId);

        // then
        verify(hashOperations, times(1)).delete("memberIdToRefreshToken", memberId.toString());
        verify(hashOperations, times(1)).delete("refreshTokenToMemberId", refreshToken);
    }

    @Test
    @DisplayName("memberId로 삭제 테스트 - 멤버가 존재하지 않는 경우")
    void deleteByMemberId_NotFound() {
        // given
        Long memberId = 999L;
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get("memberIdToRefreshToken", memberId.toString())).thenReturn(null);

        // when
        refreshTokenRedisService.deleteByMemberId(memberId);

        // then
        verify(hashOperations, never()).delete(eq("memberIdToRefreshToken"), any());
        verify(hashOperations, never()).delete(eq("refreshTokenToMemberId"), any());
    }

    @Test
    @DisplayName("refreshToken으로 memberId 조회 테스트")
    void getMemberIdByRefreshToken() {
        // given
        String refreshToken = "test-refresh-token";
        Long memberId = 1L;
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get("refreshTokenToMemberId", refreshToken)).thenReturn(memberId);

        // when
        Long result = refreshTokenRedisService.getMemberIdByRefreshToken(refreshToken);

        // then
        assertThat(result).isEqualTo(memberId);
    }

    @Test
    @DisplayName("refreshToken으로 memberId 조회 테스트 - 토큰이 존재하지 않는 경우")
    void getMemberIdByRefreshToken_NotFound() {
        // given
        String refreshToken = "non-existent-token";
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get("refreshTokenToMemberId", refreshToken)).thenReturn(null);

        // when
        Long result = refreshTokenRedisService.getMemberIdByRefreshToken(refreshToken);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("memberId로 refreshToken 조회 테스트")
    void getRefreshTokenByMemberId() {
        // given
        String refreshToken = "test-refresh-token";
        Long memberId = 1L;
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get("memberIdToRefreshToken", memberId.toString())).thenReturn(refreshToken);

        // when
        String result = refreshTokenRedisService.getRefreshTokenByMemberId(memberId);

        // then
        assertThat(result).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("memberId로 refreshToken 조회 테스트 - 멤버가 존재하지 않는 경우")
    void getRefreshTokenByMemberId_NotFound() {
        // given
        Long memberId = 999L;
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get("memberIdToRefreshToken", memberId.toString())).thenReturn(null);

        // when
        String result = refreshTokenRedisService.getRefreshTokenByMemberId(memberId);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("전체 refreshToken 삭제 테스트")
    void clearAllRefreshTokens() {
        // when
        refreshTokenRedisService.clearAllRefreshTokens();

        // then
        verify(redisTemplate, times(1)).delete("refreshTokenToMemberId");
        verify(redisTemplate, times(1)).delete("memberIdToRefreshToken");
    }

    @Test
    @DisplayName("refreshToken 개수 조회 테스트")
    void getRefreshTokenCount() {
        // given
        long expectedCount = 5L;
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.size("refreshTokenToMemberId")).thenReturn(expectedCount);

        // when
        long result = refreshTokenRedisService.getRefreshTokenCount();

        // then
        assertThat(result).isEqualTo(expectedCount);
    }

    @Test
    @DisplayName("refreshToken 매핑 유효성 검사 테스트 - 유효한 경우")
    void validateRefreshTokenMapping_Valid() {
        // given
        Long memberId = 1L;
        String refreshToken = "test-refresh-token";
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get("memberIdToRefreshToken", memberId.toString())).thenReturn(refreshToken);
        when(hashOperations.get("refreshTokenToMemberId", refreshToken)).thenReturn(memberId);

        // when
        boolean result = refreshTokenRedisService.validateRefreshTokenMapping(memberId, refreshToken);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("refreshToken 매핑 유효성 검사 테스트 - 유효하지 않은 경우")
    void validateRefreshTokenMapping_Invalid() {
        // given
        Long memberId = 1L;
        String refreshToken = "test-refresh-token";
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get("memberIdToRefreshToken", memberId.toString())).thenReturn("different-token");
        when(hashOperations.get("refreshTokenToMemberId", refreshToken)).thenReturn(memberId);

        // when
        boolean result = refreshTokenRedisService.validateRefreshTokenMapping(memberId, refreshToken);

        // then
        assertThat(result).isFalse();
    }
}