package com.planit.planit.web.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.planit.planit.auth.jwt.JwtProvider;
import com.planit.planit.auth.oauth.CustomOAuth2UserService;
import com.planit.planit.auth.service.AuthService;
import com.planit.planit.member.repository.MemberRepository;
import com.planit.planit.web.controller.AuthController;
import com.planit.planit.web.controller.MemberController;
import com.planit.planit.web.dto.auth.OAuthLoginDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AuthController.class)
@DisplayName("AuthController - 소셜 로그인")
class AuthControllerSignInTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Nested
    @DisplayName("signIn API")
    class SignIn {
        @Test
        @DisplayName("신규 회원이면 회원가입 후 토큰이 발급된다")
        void signIn_newMember_registersAndReturnsToken() throws Exception {
            OAuthLoginDTO.LoginRequest loginRequest = OAuthLoginDTO.LoginRequest.builder()
                    .oauthProvider("GOOGLE")
                    .oauthToken("mock-id-token")
                    .build();
            OAuthLoginDTO.LoginResponse loginResponse = OAuthLoginDTO.LoginResponse.builder()
                    .isNewMember(true)
                    .isSignUpCompleted(true)
                    .email("new@example.com")
                    .name("새 유저")
                    .accessToken("access-token-xyz")
                    .refreshToken("refresh-token-xyz")
                    .build();
            given(authService.signIn(any())).willReturn(loginResponse);
            mockMvc.perform(post("/auth/sign-in")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk());
        }
    }

}
