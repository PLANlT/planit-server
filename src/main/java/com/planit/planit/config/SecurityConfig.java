package com.planit.planit.config;

import com.planit.planit.auth.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((authz) -> authz
                .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()                              // Swagger UI
                .requestMatchers(new AntPathRequestMatcher("/v3/**")).permitAll()                                      // Swagger API 문서
                .requestMatchers(new AntPathRequestMatcher("/swagger-resources/**")).permitAll()                       // Swagger 리소스
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()                              // H2 데이터베이스 콘솔 (개발용)
                .requestMatchers(new AntPathRequestMatcher("/planit/auth/sign-in", "POST")).permitAll()     // 로그인 API
                .requestMatchers(new AntPathRequestMatcher("/planit/auth/refresh", "POST")).permitAll()     // 토큰 재발급 API
                .requestMatchers(new AntPathRequestMatcher("/planit/members/terms", "POST")).permitAll()    // 약관 URL 조회 API (인증 없이 허용)
                .anyRequest().authenticated()
        );

        http.csrf(csrf -> csrf.disable())
            .headers(headers -> headers.disable())
            .sessionManagement((session) -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
