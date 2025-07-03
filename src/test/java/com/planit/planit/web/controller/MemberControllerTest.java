package com.planit.planit.web.controller;

import com.planit.planit.config.jwt.UserPrincipal;
import com.planit.planit.member.MemberRepository;
import com.planit.planit.member.enums.Role;
import com.planit.planit.member.service.MemberService;
import com.planit.planit.member.service.MemberServiceImpl;
import com.planit.planit.web.dto.auth.login.OAuthLoginDTO;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

@DisplayName("MemberController - 소셜 로그인")
@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @InjectMocks
    private MemberController memberController;

    @Mock
    private MemberServiceImpl memberService;

    @Nested
    @DisplayName("signIn 메서드는")
    class SignIn {

        @Test
        @DisplayName("기존 회원이면 isNewMember=false와 토큰을 반환한다")
        void signIn_existingMember_returnsTokenAndIsNewFalse() {
            // given
            OAuthLoginDTO.Request request = OAuthLoginDTO.Request.builder()
                    .oauthProvider("google")
                    .oauthAccessToken("123456789")
                    .build();

            OAuthLoginDTO.Response response = OAuthLoginDTO.Response.builder()
                    .isNewMember(false)
                    .email("test@example.com")
                    .name("홍길동")
                    .accessToken("access-token-abc")
                    .refreshToken("refresh-token-def")
                    .build();

            given(memberService.checkOAuthMember(any())).willReturn(response);

            // when
            ResponseEntity<OAuthLoginDTO.Response> result = memberController.signIn(request);

            // then
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertFalse(result.getBody().isNewMember());
            assertEquals("test@example.com", result.getBody().getEmail());
        }

        @Test
        @DisplayName("신규 회원이면 isNewMember=true만 반환하고 토큰은 null이다")
        void signIn_newMember_returnsIsNewTrueWithoutToken() {
            // given
            OAuthLoginDTO.Request request = OAuthLoginDTO.Request.builder()
                    .oauthProvider("google")
                    .oauthAccessToken("new-access-token")
                    .build();

            OAuthLoginDTO.Response response = OAuthLoginDTO.Response.builder()
                    .isNewMember(true)
                    .email("new@example.com")
                    .name("새 유저")
                    .accessToken(null)
                    .refreshToken(null)
                    .build();

            given(memberService.checkOAuthMember(any())).willReturn(response);

            // when
            ResponseEntity<OAuthLoginDTO.Response> result = memberController.signIn(request);

            // then
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertTrue(result.getBody().isNewMember());
            assertNull(result.getBody().getAccessToken());
        }

        @Test
        @DisplayName("지원하지 않는 OAuth Provider일 경우 예외를 던진다")
        void signIn_invalidProvider_throwsException() {
            // given
            OAuthLoginDTO.Request request = OAuthLoginDTO.Request.builder()
                    .oauthProvider("invalid")
                    .oauthAccessToken("token")
                    .build();

            given(memberService.checkOAuthMember(any()))
                    .willThrow(new IllegalArgumentException("지원하지 않는 provider"));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                memberController.signIn(request);
            });
        }

        @Test
        @DisplayName("memberService에서 예외가 발생하면 예외가 그대로 전달된다")
        void signIn_serviceError_propagatesException() {
            // given
            OAuthLoginDTO.Request request = OAuthLoginDTO.Request.builder()
                    .oauthProvider("google")
                    .oauthAccessToken("token")
                    .build();

            given(memberService.checkOAuthMember(any()))
                    .willThrow(new RuntimeException("DB 오류"));

            // when & then
            RuntimeException e = assertThrows(RuntimeException.class, () -> {
                memberController.signIn(request);
            });
            assertEquals("DB 오류", e.getMessage());
        }
    }

    @Nested
    @DisplayName("signOut 메서드는")
    class SignOut {

        @Test
        @DisplayName("정상적으로 로그아웃되면 200 OK를 반환한다")
        void signOut_success_returnsOk() {
            // given
            Long memberId = 1L;
            UserPrincipal principal = new UserPrincipal(memberId, "test@example.com", "홍길동", Role.USER);

            // when
            ResponseEntity<Void> response = memberController.signOut(principal);

            // then
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        @DisplayName("로그인 정보가 없으면 401 Unauthorized를 반환한다")
        void signOut_noAuthentication_returnsUnauthorized() {
            // given
            UserPrincipal nullPrincipal = null;

            // when & then
            assertThrows(NullPointerException.class, () -> {
                memberController.signOut(nullPrincipal);
            });
        }

        @Test
        @DisplayName("로그아웃 도중 예외가 발생하면 예외가 그대로 던져진다")
        void signOut_serviceError_propagatesException() {
            // given
            Long memberId = 2L;
            UserPrincipal principal = new UserPrincipal(memberId, "test@example.com", "홍길동", Role.USER);

            doThrow(new RuntimeException("로그아웃 실패")).when(memberService).signOut(any());

            // when & then
            RuntimeException e = assertThrows(RuntimeException.class, () -> {
                memberController.signOut(principal);
            });
            assertEquals("로그아웃 실패", e.getMessage());
        }
    }

}
