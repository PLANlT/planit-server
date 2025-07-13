package com.planit.planit.member.service;

import com.planit.planit.common.api.member.MemberHandler;
import com.planit.planit.common.api.member.status.MemberErrorStatus;
import com.planit.planit.member.Member;
import com.planit.planit.member.association.FcmToken;
import com.planit.planit.member.enums.DailyCondition;
import com.planit.planit.member.enums.Role;
import com.planit.planit.member.enums.SignType;
import com.planit.planit.member.repository.FcmTokenRepository;
import com.planit.planit.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FcmTokenServiceImplTest {

    @Mock
    private FcmTokenRepository fcmTokenRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private FcmTokenServiceImpl fcmTokenService;

    @Test
    @DisplayName("기존 FcmToken이 존재하면 토큰 값을 갱신한다")
    void updateExistingFcmToken() {
        // given
        Long memberId = 1L;
        Member member = createMockMember(memberId);
        FcmToken existingToken = FcmToken.of(member, "old_token");

        when(fcmTokenRepository.findById(memberId)).thenReturn(Optional.of(existingToken));

        // when
        fcmTokenService.saveOrUpdateFcmToken(memberId, "new_token");

        // then
        assertThat(existingToken.getToken()).isEqualTo("new_token");
        verify(fcmTokenRepository, never()).save(existingToken); // save() 생략 시
    }

    @Test
    @DisplayName("FcmToken이 없으면 새로 생성해서 저장한다")
    void createNewFcmToken() {
        // given
        Long memberId = 2L;
        Member member = createMockMember(memberId);
        when(fcmTokenRepository.findById(memberId)).thenReturn(Optional.empty());
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        fcmTokenService.saveOrUpdateFcmToken(memberId, "new_token_123");

        // then
        verify(fcmTokenRepository).save(any(FcmToken.class));
    }

    @Test
    @DisplayName("존재하지 않는 회원이면 예외가 발생한다")
    void throwsExceptionWhenMemberNotFound() {
        // given
        Long memberId = 99L;
        when(fcmTokenRepository.findById(memberId)).thenReturn(Optional.empty());
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> fcmTokenService.saveOrUpdateFcmToken(memberId, "any"))
                .isInstanceOf(MemberHandler.class)
                .hasMessageContaining(MemberErrorStatus.MEMBER_NOT_FOUND.getCode());
    }

    @Test
    @DisplayName("토큰 값으로 FcmToken을 삭제한다")
    void deleteTokenTest() {
        // given
        String token = "test_token";

        // when
        fcmTokenService.deleteToken(token);

        // then
        verify(fcmTokenRepository).deleteByToken(token);
    }

    @Test
    @DisplayName("회원 ID로 FcmToken을 삭제한다")
    void deleteTokensByMemberIdTest() {
        // given
        Long memberId = 5L;

        // when
        fcmTokenService.deleteTokensByMemberId(memberId);

        // then
        verify(fcmTokenRepository).deleteById(memberId);
    }

    @Test
    @DisplayName("무효 토큰 리스트로 일괄 삭제한다")
    void cleanUpInvalidTokensTest() {
        // given
        List<String> invalidTokens = List.of("bad_token_1", "bad_token_2");

        // when
        fcmTokenService.cleanUpInvalidTokens(invalidTokens);

        // then
        verify(fcmTokenRepository).deleteByTokenIn(invalidTokens);
    }

    // 헬퍼 메서드
    private Member createMockMember(Long id) {
        Member member = Member.builder()
                .email("test@planit.com")
                .password("pw")
                .signType(SignType.GOOGLE)
                .memberName("푸바오")
                .role(Role.USER)
                .guiltyFreeMode(false)
                .dailyCondition(DailyCondition.DISTRESS)
                .build();
        // 테스트용으로 리플렉션 ID 세팅 (Setter 없을 경우)
        try {
            java.lang.reflect.Field idField = Member.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(member, id);
        } catch (Exception ignored) {}
        return member;
    }
}
