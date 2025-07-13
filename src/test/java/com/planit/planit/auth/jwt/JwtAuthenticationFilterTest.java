package com.planit.planit.auth.jwt;

import com.planit.planit.member.enums.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private JwtProvider jwtProvider;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = mock(JwtProperties.class);
        when(jwtProperties.getSecret()).thenReturn("test-secret-key-which-is-long-enough");
        when(jwtProperties.getExpirationMs()).thenReturn(3600000L);

        jwtProvider = new JwtProvider(jwtProperties);
        filter = new JwtAuthenticationFilter(jwtProvider);

        SecurityContextHolder.clearContext();
    }

    @Test
    @Order(1)
    @DisplayName("유효한 accessToken이 있으면 인증 객체가 SecurityContext에 등록된다")
    void 유효한_AccessToken이_있으면_인증_객체가_생성된다() throws ServletException, IOException {
        // given
        Long userId = 1L;
        String accessToken = jwtProvider.createAccessToken(userId, "user@example.com", "홍길동", Role.USER);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + accessToken); // ⬅ 수정됨
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        // when
        filter.doFilterInternal(request, response, chain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()).isTrue();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isInstanceOf(UserPrincipal.class);

        verify(chain).doFilter(request, response);
    }
}
