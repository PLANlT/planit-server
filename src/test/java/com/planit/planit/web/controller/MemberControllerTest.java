
package com.planit.planit.web.controller;

import com.planit.planit.agreement.service.AgreementService;
import com.planit.planit.common.api.ApiResponse;
import com.planit.planit.common.api.member.status.MemberSuccessStatus;
import com.planit.planit.config.jwt.UserPrincipal;
import com.planit.planit.member.enums.Role;
import com.planit.planit.member.service.MemberServiceImpl;
import com.planit.planit.web.dto.auth.login.OAuthLoginDTO;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

    @Mock
    private AgreementService agreementService;

    @Nested
    @DisplayName("signIn 메서드는")
    class SignIn {

        @Test
        @DisplayName("기존 회원이면 isNewMember=false와 토큰을 반환한다")
        void signIn_existingMember_returnsTokenAndIsNewFalse() {
            // given
            OAuthLoginDTO.Request request = OAuthLoginDTO.Request.builder()
                    .oauthProvider("google")
                    .oauthToken("123456789")
                    .build();

            OAuthLoginDTO.Response expectedResponse = OAuthLoginDTO.Response.builder()
                    .isNewMember(false)
                    .email("test@example.com")
                    .name("홍길동")
                    .accessToken("access-token-abc")
                    .refreshToken("refresh-token-def")
                    .build();

            given(memberService.signIn(request)).willReturn(expectedResponse);

            // when
            ApiResponse<OAuthLoginDTO.Response> actualResponse = memberController.signIn(request);

            // then
            assertEquals("MEMBER2000", actualResponse.getCode());
            assertNotNull(actualResponse.getData());
            assertFalse(actualResponse.getData().isNewMember());
            assertEquals("access-token-abc", actualResponse.getData().getAccessToken());
            assertEquals("refresh-token-def", actualResponse.getData().getRefreshToken());
            assertEquals("test@example.com", actualResponse.getData().getEmail());
            assertEquals("홍길동", actualResponse.getData().getName());
        }


        @Test
        @DisplayName("신규 회원이면 isNewMember=true만 반환하고 토큰은 null이다")
        void signIn_newMember_returnsIsNewTrueWithoutToken() {
            // given
            OAuthLoginDTO.Request request = OAuthLoginDTO.Request.builder()
                    .oauthProvider("google")
                    .oauthToken("new-access-token")
                    .build();

            OAuthLoginDTO.Response expectedResponse = OAuthLoginDTO.Response.builder()
                    .isNewMember(true)
                    .email("new@example.com")
                    .name("새 유저")
                    .accessToken(null)
                    .refreshToken(null)
                    .build();

            given(memberService.signIn(request)).willReturn(expectedResponse);

            // when
            ApiResponse<OAuthLoginDTO.Response> actualResponse = memberController.signIn(request);

            // then
            assertEquals("MEMBER2000", actualResponse.getCode());
            assertNotNull(actualResponse.getData());
            assertTrue(actualResponse.getData().isNewMember());
            assertNull(actualResponse.getData().getAccessToken());
            assertNull(actualResponse.getData().getRefreshToken());
            assertEquals("new@example.com", actualResponse.getData().getEmail());
            assertEquals("새 유저", actualResponse.getData().getName());
        }


        @Test
        @DisplayName("지원하지 않는 OAuth Provider일 경우 예외를 던진다")
        void signIn_invalidProvider_throwsException() {
            // given
            OAuthLoginDTO.Request request = OAuthLoginDTO.Request.builder()
                    .oauthProvider("invalid")
                    .oauthToken("token")
                    .build();

            given(memberService.signIn(any()))
                    .willThrow(new IllegalArgumentException("지원하지 않는 provider"));

            // when & then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                memberController.signIn(request);
            });
            assertEquals("지원하지 않는 provider", exception.getMessage());
        }


        @Test
        @DisplayName("memberService에서 예외가 발생하면 예외가 그대로 전달된다")
        void signIn_serviceError_propagatesException() {
            // given
            OAuthLoginDTO.Request request = OAuthLoginDTO.Request.builder()
                    .oauthProvider("google")
                    .oauthToken("token")
                    .build();

            given(memberService.signIn(any()))
                    .willThrow(new RuntimeException("DB 오류"));

            // when & then
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                memberController.signIn(request);
            });
            assertEquals("DB 오류", exception.getMessage());
        }

    }

//    @Nested
//    @DisplayName("signOut 메서드는")
//    class SignOut {
//
//        @Test
//        @DisplayName("정상적으로 로그아웃되면 200 OK를 반환한다")
//        void signOut_success_returnsOk() {
//            // given
//            Long memberId = 1L;
//            UserPrincipal principal = new UserPrincipal(memberId, "test@example.com", "홍길동", Role.USER);
//
//            // when
//            ResponseEntity<Void> response = memberController.signOut(principal);
//
//            // then
//            assertEquals(HttpStatus.OK, response.getStatusCode());
//        }
//
//        @Test
//        @DisplayName("로그인 정보가 없으면 401 Unauthorized를 반환한다")
//        void signOut_noAuthentication_returnsUnauthorized() {
//            // given
//            UserPrincipal nullPrincipal = null;
//
//            // when & then
//            assertThrows(NullPointerException.class, () -> {
//                memberController.signOut(nullPrincipal);
//            });
//        }
//
//        @Test
//        @DisplayName("로그아웃 도중 예외가 발생하면 예외가 그대로 던져진다")
//        void signOut_serviceError_propagatesException() {
//            // given
//            Long memberId = 2L;
//            UserPrincipal principal = new UserPrincipal(memberId, "test@example.com", "홍길동", Role.USER);
//
//            doThrow(new RuntimeException("로그아웃 실패")).when(memberService).signOut(any());
//
//            // when & then
//            RuntimeException e = assertThrows(RuntimeException.class, () -> {
//                memberController.signOut(principal);
//            });
//            assertEquals("로그아웃 실패", e.getMessage());
//        }
//    }

}