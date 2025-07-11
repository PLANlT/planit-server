package com.planit.planit.web.controller.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.planit.planit.auth.FakeCustomOAuth2User;
import com.planit.planit.common.api.general.GeneralException;
import com.planit.planit.common.api.general.status.ErrorStatus;
import com.planit.planit.config.jwt.JwtProvider;
import com.planit.planit.config.oauth.CustomOAuth2UserService;
import com.planit.planit.member.enums.SignType;
import com.planit.planit.member.repository.MemberRepository;
import com.planit.planit.member.service.MemberService;
import com.planit.planit.web.controller.MemberController;
import com.planit.planit.web.dto.auth.login.OAuthLoginDTO;
import com.planit.planit.web.dto.member.term.TermAgreementDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(MemberController.class)
@DisplayName("MemberController - 소셜 로그인")
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

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
    @DisplayName("signIn API는")
    class SignIn {
        @Test
        @DisplayName("신규 회원이면 회원가입 후 토큰이 발급된다")
        void signIn_newMember_registersAndReturnsToken() throws Exception {
            OAuthLoginDTO.Request request = OAuthLoginDTO.Request.builder()
                    .oauthProvider("GOOGLE")
                    .oauthToken("mock-id-token")
                    .build();
            OAuthLoginDTO.Response response = OAuthLoginDTO.Response.builder()
                    .isNewMember(true)
                    .isSignUpCompleted(true)
                    .email("new@example.com")
                    .name("새 유저")
                    .accessToken("access-token-xyz")
                    .refreshToken("refresh-token-xyz")
                    .build();
            given(memberService.signIn(any())).willReturn(response);
            mockMvc.perform(post("/members/sign-in")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }
    }

}
