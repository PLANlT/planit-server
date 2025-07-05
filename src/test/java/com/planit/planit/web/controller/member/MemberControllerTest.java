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
        @DisplayName("기존 회원이면 200 OK와 body를 반환한다")
        void signIn_existingMember_returnsOkAndBody() throws Exception {
            // given
            TermAgreementDTO.Request termRequest = null; // 기존 회원은 약관 동의 불필요

            OAuthLoginDTO.Response response = OAuthLoginDTO.Response.builder()
                    .isNewMember(false)
                    .isSignUpCompleted(false)
                    .email("test@example.com")
                    .name("홍길동")
                    .accessToken("access-token-abc")
                    .refreshToken("refresh-token-def")
                    .build();

            given(memberService.signIn(any(), any())).willReturn(response);

            FakeCustomOAuth2User fakeUser = new FakeCustomOAuth2User(
                    Map.of("sub", "123456789", "email", "test@example.com", "name", "홍길동"),
                    SignType.GOOGLE
            );

            // when & then
            mockMvc.perform(post("/members/sign-in")
                            .contentType(MediaType.APPLICATION_JSON)
                            .requestAttr("oauthUser", fakeUser))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.code").value("MEMBER2000"))
                    .andExpect(jsonPath("$.message").value("로그인이 완료되었습니다."))
                    .andExpect(jsonPath("$.data.email").value("test@example.com"))
                    .andExpect(jsonPath("$.data.isNewMember").value(false))
                    .andExpect(jsonPath("$.data.isSignUpCompleted").value(false))
                    .andExpect(jsonPath("$.data.accessToken").value("access-token-abc"))
                    .andExpect(jsonPath("$.data.refreshToken").value("refresh-token-def"));
        }

        @Test
        @DisplayName("신규 회원이면 200 OK와 isNewMember=true인 body를 반환한다")
        void signIn_newMember_returnsOkAndBody() throws Exception {
            // given
            TermAgreementDTO.Request termRequest = null; // 신규 회원이지만 아직 약관 동의하지 않음

            OAuthLoginDTO.Response response = OAuthLoginDTO.Response.builder()
                    .isNewMember(true)
                    .isSignUpCompleted(false)
                    .email("new@example.com")
                    .name("새 유저")
                    .accessToken(null)
                    .refreshToken(null)
                    .build();

            given(memberService.signIn(any(), any())).willReturn(response);

            FakeCustomOAuth2User fakeUser = new FakeCustomOAuth2User(
                    Map.of("sub", "987654321", "email", "new@example.com", "name", "새 유저"),
                    SignType.GOOGLE
            );

            // when & then
            mockMvc.perform(post("/members/sign-in")
                            .contentType(MediaType.APPLICATION_JSON)
                            .requestAttr("oauthUser", fakeUser))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.code").value("MEMBER2001"))
                    .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."))
                    .andExpect(jsonPath("$.data.isNewMember").value(true))
                    .andExpect(jsonPath("$.data.isSignUpCompleted").value(false))
                    .andExpect(jsonPath("$.data.email").value("new@example.com"))
                    .andExpect(jsonPath("$.data.name").value("새 유저"));
        }

        @Test
        @DisplayName("약관 동의를 포함하면 회원가입 처리 후 토큰이 발급된다")
        void signIn_newMemberWithTerms_returnsTokens() throws Exception {
            // given
            TermAgreementDTO.Request termRequest = TermAgreementDTO.Request.builder()
                    .termOfUse(LocalDateTime.now())
                    .termOfPrivacy(LocalDateTime.now())
                    .termOfInfo(LocalDateTime.now())
                    .overFourteen(LocalDateTime.now())
                    .build();

            OAuthLoginDTO.Response response = OAuthLoginDTO.Response.builder()
                    .isNewMember(true)  // 신규 회원이지만 회원가입 완료
                    .isSignUpCompleted(true)
                    .email("new@example.com")
                    .name("새 유저")
                    .accessToken("access-token-xyz")
                    .refreshToken("refresh-token-xyz")
                    .build();

            given(memberService.signIn(any(), any())).willReturn(response);

            FakeCustomOAuth2User fakeUser = new FakeCustomOAuth2User(
                    Map.of("sub", "987654321", "email", "new@example.com", "name", "새 유저"),
                    SignType.GOOGLE
            );

            // when & then
            mockMvc.perform(post("/members/sign-in")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(termRequest))
                            .requestAttr("oauthUser", fakeUser))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.code").value("MEMBER2001"))
                    .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."))
                    .andExpect(jsonPath("$.data.isNewMember").value(true))
                    .andExpect(jsonPath("$.data.isSignUpCompleted").value(true))
                    .andExpect(jsonPath("$.data.accessToken").value("access-token-xyz"))
                    .andExpect(jsonPath("$.data.refreshToken").value("refresh-token-xyz"));
        }

        @Test
        @DisplayName("memberService에서 GeneralException이 발생하면 해당 에러 상태를 반환한다")
        void signIn_generalException_returnsErrorStatus() throws Exception {
            // given
            GeneralException generalException = new GeneralException(ErrorStatus.NOT_FOUND);
            given(memberService.signIn(any(), any())).willThrow(generalException);

            FakeCustomOAuth2User fakeUser = new FakeCustomOAuth2User(
                    Map.of("sub", "notfound", "email", "notfound@example.com", "name", "Not Found User"),
                    SignType.GOOGLE
            );

            // when & then
            mockMvc.perform(post("/members/sign-in")
                            .contentType(MediaType.APPLICATION_JSON)
                            .requestAttr("oauthUser", fakeUser))
                    .andExpect(jsonPath("$.isSuccess").value(false))
                    .andExpect(jsonPath("$.code").value("COMMON4004"))
                    .andExpect(jsonPath("$.message").value("리소스를 찾을 수 없습니다."));
        }
    }

}
