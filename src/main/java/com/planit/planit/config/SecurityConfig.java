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

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //TODO: 배포 시 사용 범위 제한
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                // **여기에 인증 없이 허용할 URL 패턴을 명확히 지정합니다.**
                                .requestMatchers(
                                        "/swagger-ui/**",          // Swagger UI
                                        "/v3/**",                  // Swagger API 문서
                                        "/swagger-resources/**",   // Swagger 리소스
                                        "/h2-console/**",          // H2 데이터베이스 콘솔 (개발용)
                                        "/auth/sign-in",           // 로그인 API
                                        "/auth/refresh",           // 토큰 재발급 API
                                        "/members/terms"           // 약관 URL 조회 API (인증 없이 허용)
                                ).permitAll()
                                .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
