package com.planit.planit.auth.jwt;

import com.planit.planit.common.api.general.status.ErrorResponse;
import com.planit.planit.common.api.general.status.ErrorStatus;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // 헤더가 없으면 그냥 다음 필터로 넘김
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring("Bearer ".length());

        // 토큰이 유효하지 않으면 401 에러 반환
        if (!jwtProvider.validateToken(token)) {
            setErrorStatus(response, ErrorStatus.UNAUTHORIZED);
            return;
        }

        // UserPrincipal을 생성하고, 인증 정보를 SecurityContextHolder에 설정
        UserPrincipal userPrincipal = jwtProvider.getUserPrincipal(token);

        // 비밀번호는 필요하지 않으므로 null로 설정
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("JWT_:FLT_:AUTH:::Authentication established successfully, userPrincipal({})", userPrincipal);

        filterChain.doFilter(request, response); // 다음 필터로 요청을 전달
    }

    private void setErrorStatus(HttpServletResponse response, ErrorResponse errorResponse) throws IOException {
        response.setStatus(errorResponse.getErrorStatus().value());
        response.getWriter().write(errorResponse.getMessage());
    }
}

