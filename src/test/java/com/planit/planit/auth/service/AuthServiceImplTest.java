package com.planit.planit.auth.service;

import com.planit.planit.auth.jwt.JwtProvider;
import com.planit.planit.auth.oauth.SocialTokenVerifier;
import com.planit.planit.common.api.general.GeneralException;
import com.planit.planit.common.api.member.MemberHandler;
import com.planit.planit.common.api.token.TokenHandler;
import com.planit.planit.member.Member;
import com.planit.planit.member.association.SignedMember;
import com.planit.planit.member.enums.Role;
import com.planit.planit.member.enums.SignType;
import com.planit.planit.member.repository.MemberRepository;
import com.planit.planit.member.repository.NotificationRepository;
import com.planit.planit.member.service.MemberServiceImpl;
import com.planit.planit.web.dto.auth.OAuthLoginDTO;
import com.planit.planit.web.dto.auth.TokenRefreshDTO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    @Mock
    private MemberServiceImpl memberService;

    @Mock
    private JwtProvider jwtProvider;

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
        @DisplayName("신규 회원이면 회원가입 후 signUp 토큰을 반환한다")
        void signIn_newMember_registersAndReturnsToken() throws Exception {
            OAuthLoginDTO.LoginRequest loginRequest = OAuthLoginDTO.LoginRequest.builder()
                    .oauthProvider("GOOGLE")
                    .oauthToken("mock-id-token")
                    .build();
            Member saved = Member.builder()
                    .id(99L)
                    .email("newbie@planit.com")
                    .memberName("뉴비")
                    .role(Role.USER)
                    .signType(SignType.GOOGLE)
                    .guiltyFreeMode(false)
                    .password("")
                    .build();
            SignedMember signedMember = SignedMember.of(saved, true);

            given(memberService.getSignedMemberByUserInfo(
                    "newbie@planit.com", "뉴비", SignType.GOOGLE
            )).willReturn(signedMember);
            given(jwtProvider.createSignUpToken(any())).willReturn("signup-token");
            given(socialTokenVerifier.verify(anyString(), anyString()))
                    .willReturn(new SocialTokenVerifier.SocialUserInfo("newbie@planit.com", "뉴비"));
            OAuthLoginDTO.LoginResponse loginResponse = authServiceImpl.signIn(loginRequest);

            //then
            assertThat(loginResponse.isNewMember()).isTrue();
            assertThat(loginResponse.getEmail()).isEqualTo("newbie@planit.com");
            assertThat(loginResponse.getAccessToken()).isEqualTo("signup-token");
        }

        @Test
        @DisplayName("로그인 시 아이디(이메일)가 null이면 오류를 반환한다") // DisplayName 추가
        public void 아이디가_null이면_오류반환(){

            //given

            //when

            //then
        }

        @Test
        @DisplayName("로그인 시 비밀번호가 null이면 오류를 반환한다") // DisplayName 추가
        public void 비밀번호_null이면_오류반환(){

            //given

            //when

            //then
        }

        @Test
        @DisplayName("로그인 시 아이디(이메일) 형식이 유효하지 않으면 오류를 반환한다") // DisplayName 추가
        public void 아이디_형식_이상하면_오류(){

            //given

            //when

            //then
        }

        @Test
        @DisplayName("로그인 시 비밀번호 형식이 유효하지 않으면 오류를 반환한다") // DisplayName 추가
        public void 비밀번호_형식_이상하면_오류(){

            //given

            //when

            //then
        }

        @Test
        @DisplayName("탈퇴한 계정으로 로그인 시도 시 오류를 반환한다") // DisplayName 추가
        public void 탈퇴한_계정_로그인시_오류(){

            //given

            //when

            //then
        }

        @Test
        @DisplayName("존재하지 않는 아이디(이메일)로 로그인 시 오류를 반환한다") // DisplayName 추가
        public void 아이디가_없으면_오류(){

            //given

            //when

            //then
        }

        @Test
        @DisplayName("아이디는 존재하지만 비밀번호가 비어있으면 오류를 반환한다") // DisplayName 추가
        public void 아이디있고_비밀번호없으면_오류(){

            //given

            //when

            //then
        }

        @Test
        @DisplayName("아이디는 존재하지만 비밀번호가 일치하지 않으면 오류를 반환한다") // DisplayName 추가
        public void 아이디있고_비밀번호틀리면_오류(){

            //given

            //when

            //then
        }

        @Test
        @DisplayName("다 맞을 때 로그인 성공")
        public void 정상_로그인(){

            //given

            //when

            //then

        }

        @Test
        @DisplayName("성공적인 회원가입 시 새로운 회원을 생성하고 성공 응답을 반환한다")
        public void 회원가입_성공() {
            // Given (Mock 설정: 이메일 중복 없음, save 성공)
            // When (authService.register() 호출)
            // Then (예상 결과 검증: 반환값, save 호출 여부 등)
        }

        @Test
        @DisplayName("회원가입 시 이미 존재하는 이메일이면 오류를 반환한다")
        public void 회원가입_실패_중복_이메일() {
            // Given (Mock 설정: findByEmail 호출 시 Optional.of(기존_회원) 반환)
            // When (authService.register() 호출)
            // Then (예상 예외 검증: DUPLICATE_EMAIL 오류 등)
        }

        @Test
        @DisplayName("회원가입 시 이메일이 null이면 오류를 반환한다")
        public void 회원가입_실패_이메일_null() {
            // Given (requestDto에 email = null 설정)
            // When (authService.register() 호출)
            // Then (예상 예외 검증: Input Validation 오류 등)
        }

        @Test
        @DisplayName("회원가입 시 이메일 형식이 유효하지 않으면 오류를 반환한다")
        public void 회원가입_실패_이메일_형식_오류() {
            // Given (requestDto에 유효하지 않은 이메일 형식 설정)
            // When (authService.register() 호출)
            // Then (예상 예외 검증: Input Validation 오류 등)
        }

        @Test
        @DisplayName("회원가입 시 비밀번호가 null이면 오류를 반환한다")
        public void 회원가입_실패_비밀번호_null() {
            // Given (requestDto에 password = null 설정)
            // When (authService.register() 호출)
            // Then (예상 예외 검증: Input Validation 오류 등)
        }

        @Test
        @DisplayName("회원가입 시 비밀번호 형식이 유효하지 않으면 오류를 반환한다")
        public void 회원가입_실패_비밀번호_형식_오류() {
            // Given (requestDto에 유효하지 않은 비밀번호 형식 설정: 짧거나, 특정 문자 미포함 등)
            // When (authService.register() 호출)
            // Then (예상 예외 검증: Input Validation 오류 등)
        }

        @Test
        @DisplayName("회원가입 시 사용자 이름이 null이거나 비어있으면 오류를 반환한다")
        public void 회원가입_실패_사용자_이름_누락() {
            // Given (requestDto에 memberName = null 또는 "" 설정)
            // When (authService.register() 호출)
            // Then (예상 예외 검증: Input Validation 오류 등)
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
            authServiceImpl.signOut(memberId, accessToken);

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
            assertThatCode(() -> authServiceImpl.signOut(memberId, accessToken))
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
                .password("password")
                .memberName("yyy")
                .guiltyFreeMode(false)
                .role(Role.USER)
                .signType(SignType.GOOGLE)
                .build();

        @Test
        @DisplayName("유효한 리프레시 토큰이면 액세스 토큰을 새로 발급한다")
        void refreshAccessToken_validToken_returnsNewAccessToken() {
            // given
            given(jwtProvider.getId(validRefreshToken)).willReturn(memberId);
            given(refreshTokenRedisService.getRefreshTokenByMemberId(memberId)).willReturn(validRefreshToken);
            given(memberService.getSignedMemberById(memberId)).willReturn(SignedMember.of(member, false));
            given(jwtProvider.createAccessToken(
                    member.getId(), member.getEmail(), member.getMemberName(), member.getRole())
            ).willReturn("newAccessToken");

            // when
            TokenRefreshDTO.Response response = authServiceImpl.refreshAccessToken(validRefreshToken);

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
            assertThatThrownBy(() -> authServiceImpl.refreshAccessToken(validRefreshToken))
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
            assertThatThrownBy(() -> authServiceImpl.refreshAccessToken(validRefreshToken))
                    .isInstanceOf(TokenHandler.class)
                    .hasMessageContaining("4003");
        }

        @Test
        @DisplayName("Member가 존재하지 않으면 예외가 발생한다")
        void refreshAccessToken_memberNotFound_throwsException() {
            // given
            given(jwtProvider.getId(validRefreshToken)).willReturn(memberId);
            given(refreshTokenRedisService.getRefreshTokenByMemberId(memberId)).willReturn(validRefreshToken);
            given(memberService.getSignedMemberById(memberId)).willThrow(MemberHandler.class);

            // when & then
            assertThatThrownBy(() -> authServiceImpl.refreshAccessToken(validRefreshToken))
                    .isInstanceOf(MemberHandler.class);
        }
    }
}
