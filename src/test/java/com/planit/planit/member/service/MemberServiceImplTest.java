package com.planit.planit.member.service;

import com.planit.planit.auth.FakeCustomOAuth2User;
import com.planit.planit.config.jwt.JwtProvider;
import com.planit.planit.member.Member;
import com.planit.planit.member.repository.MemberRepository;
import com.planit.planit.member.repository.TermRepository;
import com.planit.planit.member.enums.Role;
import com.planit.planit.member.enums.SignType;
import com.planit.planit.web.dto.auth.login.OAuthLoginDTO;
import com.planit.planit.redis.service.RefreshTokenRedisService;
import com.planit.planit.redis.service.BlacklistTokenRedisService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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

    @Nested
    @DisplayName("signIn 메서드")
    class SignIn {
        @Test
        @DisplayName("기존 회원이면 isNewMember=false와 토큰을 반환한다")
        void signIn_existingMember_returnsTokenAndIsNewFalse() {
            // given
            Map<String, Object> attributes = Map.of(
                    "email", "exist@planit.com",
                    "name", "플래닛"
            );
            FakeCustomOAuth2User user = new FakeCustomOAuth2User(attributes, SignType.GOOGLE);

            Member existing = Member.builder()
                    .email("exist@planit.com")
                    .memberName("플래닛")
                    .role(Role.USER)
                    .signType(SignType.GOOGLE)
                    .guiltyFreeMode(false)
                    .password("")
                    .build();

            given(memberRepository.findByEmail("exist@planit.com"))
                    .willReturn(Optional.of(existing));
            given(jwtProvider.createAccessToken(any(), any(), any(), any()))
                    .willReturn("access-token");
            given(jwtProvider.createRefreshToken(any(), any(), any(), any()))
                    .willReturn("refresh-token");

            // when
            OAuthLoginDTO.Response response = memberServiceImpl.signIn(user, null);

            // then
            assertThat(response.isNewMember()).isFalse();
            assertThat(response.getEmail()).isEqualTo("exist@planit.com");
            assertThat(response.getAccessToken()).isEqualTo("access-token");
            assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        }

        @Test
        @DisplayName("신규 회원이 약관 동의 없이 로그인 시도하면 isNewMember=true만 반환한다")
        void signIn_newMemberWithoutAgreement_returnsIsNewTrue() {
            // given
            Map<String, Object> attributes = Map.of(
                    "email", "newbie@gmail.com",
                    "name", "뉴비"
            );
            FakeCustomOAuth2User user = new FakeCustomOAuth2User(attributes, SignType.GOOGLE);

            given(memberRepository.findByEmail("newbie@gmail.com"))
                    .willReturn(Optional.empty());

            // when
            OAuthLoginDTO.Response response = memberServiceImpl.signIn(user, null);

            // then
            assertThat(response.isNewMember()).isTrue();
            assertThat(response.getEmail()).isEqualTo("newbie@gmail.com");
            assertThat(response.getAccessToken()).isNull();
            assertThat(response.getRefreshToken()).isNull();
        }

        @Test
        @DisplayName("신규 회원이 약관 동의 후 로그인 시도하면 회원가입 처리 및 토큰을 반환한다")
        void signIn_newMemberWithAgreement_registersAndReturnsToken() {
            // given
            Map<String, Object> attributes = Map.of(
                    "email", "newbie2@gmail.com",
                    "name", "뉴비2"
            );
            FakeCustomOAuth2User user = new FakeCustomOAuth2User(attributes, SignType.GOOGLE);

            given(memberRepository.findByEmail("newbie2@gmail.com"))
                    .willReturn(Optional.empty());

            Member saved = Member.builder()
                    .email("newbie2@gmail.com")
                    .memberName("뉴비2")
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

            var agreement = com.planit.planit.web.dto.member.term.TermAgreementDTO.Request.builder()
                    .termOfUse(LocalDateTime.now())
                    .termOfPrivacy(LocalDateTime.now())
                    .build();

            // when
            OAuthLoginDTO.Response response = memberServiceImpl.signIn(user, agreement);

            // then
            assertThat(response.isNewMember()).isFalse();
            assertThat(response.getEmail()).isEqualTo("newbie2@gmail.com");
            assertThat(response.getAccessToken()).isEqualTo("access-token2");
            assertThat(response.getRefreshToken()).isEqualTo("refresh-token2");
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
}

