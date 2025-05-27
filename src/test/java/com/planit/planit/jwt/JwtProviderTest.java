package com.planit.planit.jwt;

import com.planit.planit.config.jwt.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.*;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        String secretKey = "my-super-secret-key-for-jwt-which-is-long-enough";
        long expiration = 3600000; // 1시간
        jwtProvider = new JwtProvider(secretKey, expiration);
    }

    @Test
    void 토큰_생성_및_파싱_테스트() {
        // given
        Long userId = 42L;
        String email = "user@example.com";
        String name = "홍길동";
        String role = "USER";

        // when
        String token = jwtProvider.createToken(userId, email, name, role);

        // then
        assertThat(jwtProvider.validateToken(token)).isTrue();
        assertThat(jwtProvider.getId(token)).isEqualTo(userId);
        assertThat(jwtProvider.getEmail(token)).isEqualTo(email);
        assertThat(jwtProvider.getRole(token)).isEqualTo(role);
    }

    @Test
    void 만료된_토큰_검증_실패() throws InterruptedException {
        JwtProvider shortLivedProvider = new JwtProvider(
                "short-key-which-is-long-enough-for-test-purpose",
                1 // 1ms
        );
        String token = shortLivedProvider.createToken(1L, "a@a.com", "Test", "USER");

        Thread.sleep(10); // 토큰 만료 기다림

        assertThat(shortLivedProvider.validateToken(token)).isFalse();
    }
}
