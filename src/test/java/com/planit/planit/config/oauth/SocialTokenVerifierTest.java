package com.planit.planit.config.oauth;

import com.planit.planit.auth.oauth.SocialTokenVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SocialTokenVerifierTest {

    @Nested
    @DisplayName("Kakao 토큰 검증")
    class Kakao {
        @Test
        @DisplayName("정상 응답 시 email, name 추출")
        void verifyKakao_success() throws Exception {
            // given
            RestTemplate mockRestTemplate = Mockito.mock(RestTemplate.class);
            SocialTokenVerifier verifier = new SocialTokenVerifier(mockRestTemplate);

            String accessToken = "dummy-kakao-token";
            Map<String, Object> profile = Map.of("nickname", "카카오유저");
            Map<String, Object> kakaoAccount = Map.of(
                    "email", "kakao@planit.com",
                    "profile", profile
            );
            Map<String, Object> body = Map.of("kakao_account", kakaoAccount);

            ResponseEntity<Map> response = new ResponseEntity<>(body, HttpStatus.OK);
            Mockito.when(mockRestTemplate.exchange(
                    Mockito.anyString(),
                    Mockito.eq(HttpMethod.GET),
                    Mockito.any(HttpEntity.class),
                    Mockito.eq(Map.class)
            )).thenReturn(response);

            // when
            SocialTokenVerifier.SocialUserInfo userInfo = verifier.verify("KAKAO", accessToken);

            // then
            assertThat(userInfo.email).isEqualTo("kakao@planit.com");
            assertThat(userInfo.name).isEqualTo("카카오유저");
        }
    }

    @Nested
    @DisplayName("Naver 토큰 검증")
    class Naver {
        @Test
        @DisplayName("정상 응답 시 email, name 추출")
        void verifyNaver_success() throws Exception {
            // given
            RestTemplate mockRestTemplate = Mockito.mock(RestTemplate.class);
            SocialTokenVerifier verifier = new SocialTokenVerifier(mockRestTemplate);

            String accessToken = "dummy-naver-token";
            Map<String, Object> responseMap = Map.of(
                    "email", "naver@planit.com",
                    "name", "네이버유저"
            );
            Map<String, Object> body = Map.of("response", responseMap);

            ResponseEntity<Map> response = new ResponseEntity<>(body, HttpStatus.OK);
            Mockito.when(mockRestTemplate.exchange(
                    Mockito.anyString(),
                    Mockito.eq(HttpMethod.GET),
                    Mockito.any(HttpEntity.class),
                    Mockito.eq(Map.class)
            )).thenReturn(response);

            // when
            SocialTokenVerifier.SocialUserInfo userInfo = verifier.verify("NAVER", accessToken);

            // then
            assertThat(userInfo.email).isEqualTo("naver@planit.com");
            assertThat(userInfo.name).isEqualTo("네이버유저");
        }
    }
}
