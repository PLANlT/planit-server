package com.planit.planit.config;

import com.planit.planit.config.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter  jwtAuthenticationFilter;

    /**
     * Configures and returns the application's security filter chain.
     *
     * Allows unauthenticated access to specific endpoints such as Swagger UI, API documentation, H2 console, sign-in, authentication, and terms retrieval. All other requests require authentication. Disables CORS, CSRF protection, and HTTP headers security, and adds the JWT authentication filter before the username-password authentication filter.
     *
     * @param http the {@link HttpSecurity} to configure
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during security configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //TODO: 배포 시 사용 범위 제한
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                // **여기에 인증 없이 허용할 URL 패턴을 명확히 지정합니다.**
                                .requestMatchers(
                                        "/swagger-ui/**",          // Swagger UI
                                        "/v3/api-docs/**",         // Swagger API 문서
                                        "/swagger-resources/**",   // Swagger 리소스
                                        "/h2-console/**",          // H2 데이터베이스 콘솔 (개발용)
                                        "/members/sign-in",        // 로그인 API
                                        "/auth/**",                // 인증 관련 기타 API (예: 토큰 재발급 등)
                                        "/members/terms"           // 약관 URL 조회 API (인증 없이 허용)
                                ).permitAll()
                                .anyRequest().authenticated()
                )
                .cors(cors -> cors.disable()) //TODO: 배포 시 CORS 설정
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
