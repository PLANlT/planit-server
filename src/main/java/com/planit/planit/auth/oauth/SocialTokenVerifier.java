package com.planit.planit.auth.oauth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SocialTokenVerifier {

    private final RestTemplate restTemplate;
    @Value("${oauth.google.client-id}")
    private String googleClientId;

    @Value("${oauth.google.playground-client-id}")
    private String playgroundClientId;

    public static class SocialUserInfo {
        public final String email;
        public final String name;

        public SocialUserInfo(String email, String name) {
            this.email = email;
            this.name = name;
        }
    }

    public SocialUserInfo verify(String provider, String token) throws Exception {
        switch (provider.toUpperCase()) {
            case "GOOGLE":
                return verifyGoogle(token);
            case "KAKAO":
                return verifyKakao(token);
            case "NAVER":
                return verifyNaver(token);
            default:
                throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다: " + provider);
        }
    }

    private SocialUserInfo verifyGoogle(String idToken) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance()
        )
                .setAudience(Arrays.asList(
                        googleClientId,  // ✅ Flutter 앱용 client_id
                        playgroundClientId   // ✅ OAuth Playground client_id
                ))  //TODO: 나중엔 하나로
                .build();
        GoogleIdToken token = verifier.verify(idToken);
        if (token == null) throw new IllegalArgumentException("유효하지 않은 Google idToken");
        GoogleIdToken.Payload payload = token.getPayload();
        return new SocialUserInfo(payload.getEmail(), (String) payload.get("name"));
    }

    private SocialUserInfo verifyKakao(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                entity,
                Map.class
        );
        Map<String, Object> kakaoAccount = (Map<String, Object>) response.getBody().get("kakao_account");
        String email = (String) kakaoAccount.get("email");
        String name = (String) ((Map<String, Object>) kakaoAccount.get("profile")).get("nickname");
        return new SocialUserInfo(email, name);
    }

    private SocialUserInfo verifyNaver(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                entity,
                Map.class
        );
        Map<String, Object> responseMap = (Map<String, Object>) response.getBody().get("response");
        String email = (String) responseMap.get("email");
        String name = (String) responseMap.get("name");
        return new SocialUserInfo(email, name);
    }
}
