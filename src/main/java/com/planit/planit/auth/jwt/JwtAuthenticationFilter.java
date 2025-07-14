package com.planit.planit.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planit.planit.common.api.ApiResponse;
import com.planit.planit.common.api.general.status.ErrorStatus;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String prefix = "Bearer ";

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 헤더에서 토큰 추출
            String token = jwtProvider.resolveHeaderToken(request.getHeader(HttpHeaders.AUTHORIZATION), prefix);

            // 액세스 토큰이 유효한 경우 SecurityContextHolder에 인증 정보 저장
            if (token != null && jwtProvider.validateToken(token)) {
                if (jwtProvider.isAccessToken(token)) {
                    UserPrincipal userPrincipal = setAuthentication(token);
                    log.info("JWT_:FLT_:AUTH:::Authentication established successfully,id({}),email({}),name({}),role({})",
                            userPrincipal.getId(), userPrincipal.getEmail(), userPrincipal.getMemberName(), userPrincipal.getRole());
                }
            }

            // 다음 필터로 요청 전달
            filterChain.doFilter(request, response);

        } catch (JwtException e) {
            handleJwtException(response, e);
        }
    }

    private UserPrincipal setAuthentication(String token) {

        // UserPrincipal을 생성하고, 인증 정보를 SecurityContextHolder에 설정
        UserPrincipal userPrincipal = jwtProvider.getUserPrincipal(token);

        // 비밀번호는 필요하지 않으므로 null로 설정
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return userPrincipal;
    }

    private void handleJwtException(HttpServletResponse response, JwtException e) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                objectMapper.writeValueAsString(
                        ApiResponse.onFailure(ErrorStatus.FORBIDDEN, e.getMessage())
                )
        );
    }
}

