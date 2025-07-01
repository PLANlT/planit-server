package com.planit.planit.config;


import com.planit.planit.config.jwt.JwtAuthenticationFilter;
import com.planit.planit.config.oauth.CustomOAuth2User;
import com.planit.planit.config.oauth.CustomOAuth2UserService;
import com.planit.planit.config.oauth.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter  jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //TODO: 배포 시 사용 범위 제한
        http.authorizeHttpRequests(authorizeRequests ->
                authorizeRequests.anyRequest().permitAll() )
                .cors(cors -> cors.disable()) //TODO: 배포 시 CORS 설정
                .csrf(csrf -> csrf.disable())
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2SuccessHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
