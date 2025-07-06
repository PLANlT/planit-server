package com.planit.planit.redis.service;

import com.planit.planit.redis.service.RefreshTokenRedisService;
import com.planit.planit.redis.entity.RefreshTokenToMemberIdRedisEntity;
import com.planit.planit.redis.entity.MemberIdToRefreshTokenRedisEntity;
import com.planit.planit.redis.repository.RefreshTokenToMemberIdRedisRepository;
import com.planit.planit.redis.repository.MemberIdToRefreshTokenRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
class RefreshTokenRedisServiceTest {

    @Mock
    private RefreshTokenToMemberIdRedisRepository refreshTokenToMemberIdRedisRepository;
    @Mock
    private MemberIdToRefreshTokenRedisRepository memberIdToRefreshTokenRedisRepository;
    @InjectMocks
    private RefreshTokenRedisServiceImpl refreshTokenRedisService;

    @Test
    @DisplayName("refreshToken, memberId 양방향 저장 테스트")
    void saveRefreshToken() {
        // given
        Long memberId = 1L;
        String refreshToken = "test-refresh-token";
        when(refreshTokenToMemberIdRedisRepository.save(any(RefreshTokenToMemberIdRedisEntity.class)))
                .thenReturn(new RefreshTokenToMemberIdRedisEntity(refreshToken, memberId));
        when(memberIdToRefreshTokenRedisRepository.save(any(MemberIdToRefreshTokenRedisEntity.class)))
                .thenReturn(new MemberIdToRefreshTokenRedisEntity(memberId, refreshToken));

        // when
        refreshTokenRedisService.saveRefreshToken(memberId, refreshToken);

        // then
        verify(refreshTokenToMemberIdRedisRepository, times(1)).save(any(RefreshTokenToMemberIdRedisEntity.class));
        verify(memberIdToRefreshTokenRedisRepository, times(1)).save(any(MemberIdToRefreshTokenRedisEntity.class));
    }

    @Test
    @DisplayName("refreshToken으로 삭제 테스트")
    void deleteByRefreshToken() {
        // given
        String refreshToken = "test-refresh-token";
        Long memberId = 1L;
        when(refreshTokenToMemberIdRedisRepository.findById(refreshToken))
                .thenReturn(Optional.of(new RefreshTokenToMemberIdRedisEntity(refreshToken, memberId)));

        // when
        refreshTokenRedisService.deleteByRefreshToken(refreshToken);

        // then
        verify(refreshTokenToMemberIdRedisRepository, times(1)).deleteById(refreshToken);
        verify(memberIdToRefreshTokenRedisRepository, times(1)).deleteById(memberId);
    }

    @Test
    @DisplayName("memberId로 삭제 테스트")
    void deleteByMemberId() {
        // given
        String refreshToken = "test-refresh-token";
        Long memberId = 1L;
        when(memberIdToRefreshTokenRedisRepository.findById(memberId))
                .thenReturn(Optional.of(new MemberIdToRefreshTokenRedisEntity(memberId, refreshToken)));

        // when
        refreshTokenRedisService.deleteByMemberId(memberId);

        // then
        verify(memberIdToRefreshTokenRedisRepository, times(1)).deleteById(memberId);
        verify(refreshTokenToMemberIdRedisRepository, times(1)).deleteById(refreshToken);
    }

    @Test
    @DisplayName("refreshToken으로 memberId 조회 테스트")
    void getMemberIdByRefreshToken() {
        // given
        String refreshToken = "test-refresh-token";
        Long memberId = 1L;
        when(refreshTokenToMemberIdRedisRepository.findById(refreshToken))
                .thenReturn(Optional.of(new RefreshTokenToMemberIdRedisEntity(refreshToken, memberId)));

        // when
        Long result = refreshTokenRedisService.getMemberIdByRefreshToken(refreshToken);

        // then
        assertThat(result).isEqualTo(memberId);
    }

    @Test
    @DisplayName("memberId로 refreshToken 조회 테스트")
    void getRefreshTokenByMemberId() {
        // given
        String refreshToken = "test-refresh-token";
        Long memberId = 1L;
        when(memberIdToRefreshTokenRedisRepository.findById(memberId))
                .thenReturn(Optional.of(new MemberIdToRefreshTokenRedisEntity(memberId, refreshToken)));

        // when
        String result = refreshTokenRedisService.getRefreshTokenByMemberId(memberId);

        // then
        assertThat(result).isEqualTo(refreshToken);
    }
}