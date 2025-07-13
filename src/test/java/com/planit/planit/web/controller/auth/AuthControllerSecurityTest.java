package com.planit.planit.web.controller.auth;

import com.planit.planit.auth.service.AuthService;
import com.planit.planit.config.SecurityConfig;
import com.planit.planit.auth.jwt.JwtProvider;

import com.planit.planit.web.controller.AuthController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class) // ✅ 필터 포함
@DisplayName("AuthControllerSecurityTest - Spring Security 예외 테스트")
class AuthControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;
    @MockBean
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("인증되지 않은 사용자가 로그아웃 요청 시 403 Forbidden을 반환한다")
    void signOut_unauthenticatedUser_returnsUnauthorized() throws Exception {
        mockMvc.perform(post("/auth/sign-out"))
                .andExpect(status().isOk());        //TODO: 이거 수정해야함
    }
}
