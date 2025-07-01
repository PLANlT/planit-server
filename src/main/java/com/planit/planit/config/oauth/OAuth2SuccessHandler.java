package com.planit.planit.config.oauth;

import com.planit.planit.member.service.MemberService;
import com.planit.planit.web.dto.auth.login.converter.OAuthLoginDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final MemberService memberService;

    public OAuth2SuccessHandler(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        OAuthLoginDTO.Response loginResult = memberService.checkOAuthMember(oAuth2User);
        String encodedName = URLEncoder.encode(loginResult.getName(), StandardCharsets.UTF_8);
        String encodedEmail = URLEncoder.encode(loginResult.getEmail(), StandardCharsets.UTF_8);

        String redirectUri;
        if (loginResult.isNewMember()) {
            redirectUri = "http://localhost:8080/register.html" //TODO: 추후 수정
                    + "?email=" + encodedEmail
                    + "&name=" + encodedName;
        } else {
            redirectUri = "http://localhost:8080/login.html"    //TODO: 추후 수정
                    + "?accessToken=" + loginResult.getAccessToken()
                    + "&refreshToken=" + loginResult.getRefreshToken();
        }

        response.sendRedirect(redirectUri);
    }
}