package com.planit.planit.config.oauth;
import com.planit.planit.member.enums.Role;
import com.planit.planit.member.enums.SignType;
import com.planit.planit.member.service.MemberService;
import com.planit.planit.web.dto.auth.login.converter.OAuthLoginDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuth2SuccessHandlerTest {

    @InjectMocks
    private OAuth2SuccessHandler successHandler;


    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Mock
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

    }


    @Test
    @DisplayName("기존회원이면_로그인링크로_리디렉션")
    void 기존회원이면_login딥링크로_리디렉션한다() throws Exception {
        // given
        CustomOAuth2User principal = new CustomOAuth2User(
                mock(OAuth2User.class),
                SignType.GOOGLE,
                1L,
                "test@email.com",
                Role.USER,
                "홍길동"
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null);

        OAuthLoginDTO.Response dto = OAuthLoginDTO.Response.builder()
                .email("test@email.com")
                .name("홍길동")
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .isNewMember(false)
                .build();

        when(memberService.checkOAuthMember(principal)).thenReturn(dto);

        // when
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        String redirect = response.getRedirectedUrl();
        assertThat(redirect).startsWith("yourapp://login");
        assertThat(redirect).contains("accessToken=access-token");
        assertThat(redirect).contains("refreshToken=refresh-token");
    }

    @Test
    void 신규회원이면_register딥링크로_리디렉션한다() throws Exception {
        // given
        CustomOAuth2User principal = new CustomOAuth2User(
                mock(OAuth2User.class),
                SignType.KAKAO,
                null, // 아직 가입 안 됨
                "newuser@kakao.com",
                null,
                "카카오유저"
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null);

        OAuthLoginDTO.Response dto = OAuthLoginDTO.Response.builder()
                .email("newuser@kakao.com")
                .name("카카오유저")
                .accessToken(null)
                .refreshToken(null)
                .isNewMember(true)
                .build();

        when(memberService.checkOAuthMember(principal)).thenReturn(dto);

        // when
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        String redirect = response.getRedirectedUrl();
        assertThat(redirect).startsWith("yourapp://register");
        assertThat(redirect).contains("email=newuser@kakao.com");
        assertThat(redirect).contains("name=카카오유저");
    }
}

