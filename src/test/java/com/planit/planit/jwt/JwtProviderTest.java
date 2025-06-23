package com.planit.planit.jwt;

import com.planit.planit.config.jwt.JwtProperties;
import com.planit.planit.config.jwt.JwtProvider;
import org.junit.jupiter.api.*;


import static org.assertj.core.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JwtProviderTest {

    private JwtProvider jwtProvider;
    private JwtProperties jwtProperties;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        String secretKey = "my-super-secret-key-for-jwt-which-is-long-enough";
        long expiration = 3600000; // 1시간
        jwtProperties.setSecret(secretKey);
        jwtProperties.setExpirationMs(expiration);
        jwtProvider = new JwtProvider(jwtProperties);
    }

    @Test
    @Order(1)
    @DisplayName("토큰 생성 및 파싱 (성공)")
    void 토큰_생성_및_파싱_테스트() {
        // given
        Long userId = 42L;
        String email = "user@example.com";
        String memberName = "홍길동";
        String role = "USER";

        // when
        String token = jwtProvider.createToken(userId, email, memberName, role);

        // then
        assertThat(jwtProvider.validateToken(token)).isTrue();
        assertThat(jwtProvider.getId(token)).isEqualTo(userId);
        assertThat(jwtProvider.getEmail(token)).isEqualTo(email);
        assertThat(jwtProvider.getMemberName(token)).isEqualTo(memberName);
        assertThat(jwtProvider.getRole(token)).isEqualTo(role);
    }

    @Test
    @Order(2)
    @DisplayName("만료된 토큰 검증 (성공)")
    void 만료된_토큰_검증_실패() throws InterruptedException {
        jwtProperties.setExpirationMs(1);
        jwtProperties.setSecret("\"short-key-which-is-long-enough-for-test-purpose\"");
        JwtProvider shortLivedProvider = new JwtProvider(jwtProperties);
        String token = shortLivedProvider.createToken(1L, "a@a.com", "Test", "USER");

        Thread.sleep(10); // 토큰 만료 기다림

        assertThat(shortLivedProvider.validateToken(token)).isFalse();
    }
}
