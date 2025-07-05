package com.planit.planit.config.jwt;

import com.planit.planit.common.api.general.status.ErrorStatus;
import com.planit.planit.common.api.member.MemberHandler;
import com.planit.planit.common.api.member.status.MemberErrorStatus;
import com.planit.planit.member.Member;
import com.planit.planit.member.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(ErrorStatus.UNAUTHORIZED.getErrorStatus().value());
            response.getWriter().write(ErrorStatus.UNAUTHORIZED.getMessage());
            return;
        }

        String token = authHeader.substring("Bearer ".length());

        // 토큰이 유효하지 않으면 401 에러 반환
        if (!jwtProvider.validateToken(token)) {
            response.setStatus(ErrorStatus.UNAUTHORIZED.getErrorStatus().value());
            response.getWriter().write(ErrorStatus.UNAUTHORIZED.getMessage());
            return;
        }

        Long id = jwtProvider.getId(token);
        Member member = memberRepository.findById(id).orElseThrow(() -> {
            response.setStatus(MemberErrorStatus.MEMBER_NOT_FOUND.getErrorStatus().value());
            return new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND);
        });

        // UserPrincipal을 생성하고, 인증 정보를 SecurityContextHolder에 설정
        UserPrincipal userPrincipal = new UserPrincipal(
                member.getId(),
                member.getEmail(),
                member.getMemberName(),
                member.getRole()
        );

        // 비밀번호는 필요하지 않으므로 null로 설정
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response); // 다음 필터로 요청을 전달
    }
}

