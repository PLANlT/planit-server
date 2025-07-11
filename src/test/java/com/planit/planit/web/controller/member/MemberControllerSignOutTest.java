package com.planit.planit.web.controller.member;

import com.planit.planit.common.api.general.GeneralException;
import com.planit.planit.common.api.general.status.ErrorStatus;
import com.planit.planit.config.jwt.JwtProvider;
import com.planit.planit.config.jwt.UserPrincipal;
import com.planit.planit.config.oauth.CustomOAuth2UserService;
import com.planit.planit.member.enums.Role;
import com.planit.planit.member.repository.MemberRepository;
import com.planit.planit.member.service.MemberService;
import com.planit.planit.web.controller.MemberController;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false) // ✅ 필터 제거해서 인증 우회
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("MemberController - 로그아웃")
class MemberControllerSignOutTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;
    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private CustomOAuth2UserService customOAuth2UserService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("signOut API는")
    class Signout {

        @Test
        @Order(1)
        @DisplayName("인증된 사용자가 로그아웃 요청 시 200 OK를 반환한다")
        void signOut_authenticatedUser_returnsOk() throws Exception {
            UserPrincipal userPrincipal = new UserPrincipal(1L, "test@example.com", "홍길동", Role.USER);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            mockMvc.perform(post("/members/sign-out")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer test-token"))
                    .andExpect(status().isOk());
        }

        @Test
        @Order(2)
        @DisplayName("로그아웃 시 memberService의 signOut 메서드가 호출된다")
        void signOut_callsMemberServiceSignOut() throws Exception {
            UserPrincipal userPrincipal = new UserPrincipal(1L, "test@example.com", "홍길동", Role.USER);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            mockMvc.perform(post("/members/sign-out")
                            .header("Authorization", "Bearer test-token"))
                    .andExpect(status().isOk());

            verify(memberService).signOut(1L, "test-token");
        }


        @Test
        @Order(4)
        @DisplayName("로그아웃 성공 시 응답 본문이 비어있다")
        void signOut_success_returnsEmptyBody() throws Exception {
            UserPrincipal userPrincipal = new UserPrincipal(1L, "test@example.com", "홍길동", Role.USER);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            mockMvc.perform(post("/members/sign-out")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer test-token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("MEMBER2002"))
                    .andExpect(jsonPath("$.message").value("로그아웃이 완료되었습니다."))
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @Order(5)
        @DisplayName("memberService에서 예외 발생 시 해당 예외를 그대로 전파한다")
        void signOut_memberServiceException_propagatesException() throws Exception {
            UserPrincipal userPrincipal = new UserPrincipal(1L, "test@example.com", "홍길동", Role.USER);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            GeneralException generalException = new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
            org.mockito.BDDMockito.willThrow(generalException).given(memberService).signOut(any(), any());

            mockMvc.perform(post("/members/sign-out")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer test-token"))
                    .andExpect(jsonPath("$.isSuccess").value(false))
                    .andExpect(jsonPath("$.code").value("COMMON5000"))
                    .andExpect(jsonPath("$.message").value("서버 내부 오류입니다."));
        }
    }
}
