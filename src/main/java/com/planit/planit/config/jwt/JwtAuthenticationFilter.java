package com.planit.planit.config.jwt;

import com.planit.planit.common.api.member.MemberHandler;
import com.planit.planit.common.api.member.status.MemberErrorStatus;
import com.planit.planit.member.Member;
import com.planit.planit.member.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;


    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring("Bearer ".length());

        if (jwtProvider.validateToken(token)) {
            Long id = jwtProvider.getId(token);
            Member member = memberRepository.findById(id).orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));

            UserPrincipal userPrincipal = new UserPrincipal(
                    member.getId(),
                    member.getEmail(),
                    member.getMemberName(),
                    member.getRole()
            );

            UsernamePasswordAuthenticationToken authenthication = new UsernamePasswordAuthenticationToken(userPrincipal, userPrincipal.getPassword(), userPrincipal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenthication);
        }
    filterChain.doFilter(request, response);
    }
}
