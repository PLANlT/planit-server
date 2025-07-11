package com.planit.planit.member.service;

import com.planit.planit.auth.FakeCustomOAuth2User;
import com.planit.planit.common.api.general.GeneralException;
import com.planit.planit.common.api.token.TokenHandler;
import com.planit.planit.common.api.token.status.TokenErrorStatus;
import com.planit.planit.config.jwt.JwtProvider;
import com.planit.planit.member.Member;
import com.planit.planit.member.repository.MemberRepository;
import com.planit.planit.member.repository.TermRepository;
import com.planit.planit.member.enums.Role;
import com.planit.planit.member.enums.SignType;
import com.planit.planit.web.dto.auth.login.OAuthLoginDTO;
import com.planit.planit.redis.service.RefreshTokenRedisService;
import com.planit.planit.redis.service.BlacklistTokenRedisService;
import com.planit.planit.config.oauth.SocialTokenVerifier;
import com.planit.planit.web.dto.auth.login.TokenRefreshDTO;
import org.mockito.MockedStatic;

import com.planit.planit.web.dto.member.term.TermAgreementDTO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class MemberServiceImplTest {

    @InjectMocks
    private MemberServiceImpl memberServiceImpl;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private TermRepository termRepository;

    @Mock
    private RefreshTokenRedisService refreshTokenRedisService;

    @Mock
    private BlacklistTokenRedisService blacklistTokenRedisService;

    @Mock
    private SocialTokenVerifier socialTokenVerifier;

    @Nested
    @DisplayName("signIn 메서드")
    class SignIn {
        @Test
        @DisplayName("신규 회원이면 회원가입 후 토큰을 반환한다")
        void signIn_newMember_registersAndReturnsToken() throws Exception {
            OAuthLoginDTO.Request request = OAuthLoginDTO.Request.builder()
                    .oauthProvider("GOOGLE")
                    .oauthToken("mock-id-token")
                    .build();
            given(memberRepository.findByEmail("newbie@planit.com"))
                    .willReturn(Optional.empty());
            Member saved = Member.builder()
                    .id(99L)
                    .email("newbie@planit.com")
                    .memberName("뉴비")
                    .role(Role.USER)
                    .signType(SignType.GOOGLE)
                    .guiltyFreeMode(false)
                    .password("")
                    .build();
            given(memberRepository.save(any(Member.class))).willReturn(saved);
            given(jwtProvider.createAccessToken(any(), any(), any(), any()))
                    .willReturn("access-token2");
            given(jwtProvider.createRefreshToken(any(), any(), any(), any()))
                    .willReturn("refresh-token2");
            given(socialTokenVerifier.verify(anyString(), anyString()))
                    .willReturn(new SocialTokenVerifier.SocialUserInfo("newbie@planit.com", "뉴비"));
            OAuthLoginDTO.Response response = memberServiceImpl.signIn(request);

            //then
            verify(refreshTokenRedisService).saveRefreshToken(null, "refresh-token2");  //영속성 때문에 저장 안됨
            assertThat(response.isNewMember()).isTrue();
            assertThat(response.getEmail()).isEqualTo("newbie@planit.com");
            assertThat(response.getAccessToken()).isEqualTo("access-token2");
        }
    }

    @Nested
    @DisplayName("로그아웃")
    class Logout {
        @Test
        @DisplayName("로그아웃 시 accessToken 블랙리스트 저장 및 refreshToken 양방향 삭제가 호출된다")
        void logout_callsBlacklistAndRefreshTokenDelete() {
            // given
            Long memberId = 1L;
            String accessToken = "access-token";
            long ttl = 1000L;
            given(jwtProvider.getRemainingValidity(accessToken)).willReturn(ttl);

            // when
            memberServiceImpl.signOut(memberId, accessToken);

            // then
            verify(jwtProvider).getRemainingValidity(accessToken);
            verify(blacklistTokenRedisService).blacklistAccessToken(accessToken, ttl);
            verify(refreshTokenRedisService).deleteByMemberId(memberId);
        }

        @Test
        @DisplayName("정상적으로 로그아웃 처리가 된다면 예외 없이 동작한다")
        void logout_success_doesNotThrow() {
            // given
            Long memberId = 1L;
            String accessToken = "access-token";
            given(jwtProvider.getRemainingValidity(accessToken)).willReturn(1000L);

            // when & then
            assertThatCode(() -> memberServiceImpl.signOut(memberId, accessToken))
                    .doesNotThrowAnyException();
        }

    }

    @Nested
    @DisplayName("refreshAccessToken 메서드")
    class RefreshAccessToken {

        private final String validRefreshToken = "valid.refresh.token";
        private final Long memberId = 99L;
        private final Member member = Member.builder()
                .id(memberId)
                .email("test@example.com")
                .memberName("홍길동")
                .role(Role.USER)
                .build();

        @Test
        @DisplayName("유효한 리프레시 토큰이면 액세스 토큰을 새로 발급한다")
        void refreshAccessToken_validToken_returnsNewAccessToken() {
            // given
            given(jwtProvider.getId(validRefreshToken)).willReturn(memberId);
            given(refreshTokenRedisService.getRefreshTokenByMemberId(memberId)).willReturn(validRefreshToken);
            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
            given(jwtProvider.createAccessToken(
                    member.getId(), member.getEmail(), member.getMemberName(), member.getRole())
            ).willReturn("newAccessToken");

            // when
            TokenRefreshDTO.Response response = memberServiceImpl.refreshAccessToken(validRefreshToken);

            // then
            assertThat(response.getAccessToken()).isEqualTo("newAccessToken");
            assertThat(response.getRefreshToken()).isEqualTo(validRefreshToken);
        }

        @Test
        @DisplayName("만료된 리프레시 토큰이면 예외가 발생한다")
        void refreshAccessToken_invalidToken_throwsException() {
            // given
            given(jwtProvider.isTokenExpired(validRefreshToken)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> memberServiceImpl.refreshAccessToken(validRefreshToken))
                    .isInstanceOf(TokenHandler.class)
                    .hasMessageContaining("4003");
        }

        @Test
        @DisplayName("Redis에 저장된 토큰과 일치하지 않으면 4003 예외가 발생한다")
        void refreshAccessToken_mismatchStoredToken_throwsException() {
            // given
            given(jwtProvider.getId(validRefreshToken)).willReturn(memberId);
            given(refreshTokenRedisService.getRefreshTokenByMemberId(memberId)).willReturn("differentToken");

            // when & then
            assertThatThrownBy(() -> memberServiceImpl.refreshAccessToken(validRefreshToken))
                    .isInstanceOf(TokenHandler.class)
                    .hasMessageContaining("4003");
        }

        @Test
        @DisplayName("Member가 존재하지 않으면 예외가 발생한다")
        void refreshAccessToken_memberNotFound_throwsException() {
            // given
            given(jwtProvider.getId(validRefreshToken)).willReturn(memberId);
            given(refreshTokenRedisService.getRefreshTokenByMemberId(memberId)).willReturn(validRefreshToken);
            given(memberRepository.findById(memberId)).willReturn(Optional.empty());
            // when & then
            assertThatThrownBy(() -> memberServiceImpl.refreshAccessToken(validRefreshToken))
                    .isInstanceOf(GeneralException.class)
                    .hasMessageContaining("MEMBER");
        }
    }

}

