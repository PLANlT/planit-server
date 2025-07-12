package com.planit.planit.auth.jwt;

import com.planit.planit.common.api.general.status.ErrorResponse;
import com.planit.planit.common.api.general.status.ErrorStatus;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;

    private static final String prefix = "Bearer ";

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 헤더에서 토큰 추출
        String token = jwtProvider.resolveHeaderToken(request.getHeader(HttpHeaders.AUTHORIZATION), prefix);
        log.info("JWT Token: {}", token);

        // 토큰이 유효한 경우 SecurityContextHolder에 인증 정보 저장
        if (token != null && jwtProvider.validateToken(token)) {
            UserPrincipal userPrincipal = setAuthentication(token);
            log.info("JWT_:FLT_:AUTH:::Authentication established successfully, userPrincipal({})", userPrincipal);
        }

        filterChain.doFilter(request, response); // 다음 필터로 요청을 전달
    }

    private UserPrincipal setAuthentication(HttpServletRequest request, String token) {

        // UserPrincipal을 생성하고, 인증 정보를 SecurityContextHolder에 설정
        UserPrincipal userPrincipal = jwtProvider.getUserPrincipal(token);

        // 비밀번호는 필요하지 않으므로 null로 설정
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return userPrincipal;
    }

    private void setErrorStatus(HttpServletResponse response, ErrorResponse errorResponse) throws IOException {
        response.setStatus(errorResponse.getErrorStatus().value());
        response.getWriter().write(errorResponse.getMessage());
    }
}

