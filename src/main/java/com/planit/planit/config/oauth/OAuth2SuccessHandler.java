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

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private MemberService memberService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        OAuthLoginDTO.Response loginResult = memberService.checkOAuthMember(oAuth2User);

        String redirectUri;
        if (loginResult.isNewMember()) {
            redirectUri = "yourapp://register"
                    + "?email=" + loginResult.getEmail()
                    + "&name=" + loginResult.getName();
        } else {
            redirectUri = "yourapp://login"
                    + "?accessToken=" + loginResult.getAccessToken()
                    + "&refreshToken=" + loginResult.getRefreshToken();
        }

        response.sendRedirect(redirectUri);
    }
}