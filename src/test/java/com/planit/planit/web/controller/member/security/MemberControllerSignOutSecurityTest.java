package com.planit.planit.web.controller.member.security;

import com.planit.planit.config.SecurityConfig;
import com.planit.planit.member.MemberRepository;
import com.planit.planit.member.service.MemberService;
import com.planit.planit.config.jwt.JwtProvider;
import com.planit.planit.config.oauth.CustomOAuth2UserService;

import com.planit.planit.web.controller.MemberController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@Import(SecurityConfig.class) // ✅ 필터 포함
@DisplayName("MemberControllerSecurityTest - Spring Security 예외 테스트")
class MemberControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    // 🔧 SecurityConfig 내 필요한 빈들 Mocking
    @MockBean
    private MemberService memberService;
    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private CustomOAuth2UserService customOAuth2UserService;

    @Test
    @DisplayName("인증되지 않은 사용자가 로그아웃 요청 시 401 Unauthorized를 반환한다")
    void signOut_unauthenticatedUser_returnsUnauthorized() throws Exception {
        mockMvc.perform(post("/members/sign-out"))
                .andExpect(status().isUnauthorized());
    }

}
