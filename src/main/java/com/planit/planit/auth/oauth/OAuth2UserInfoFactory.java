package com.planit.planit.auth.oauth;

import com.planit.planit.member.enums.SignType;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo of(SignType signType, Map<String, Object> attributes) {
        return switch (signType) {
            case GOOGLE -> new GoogleUserInfo(attributes);
            case NAVER -> new NaverUserInfo(attributes);
            case KAKAO -> new KakaoUserInfo(attributes);
        };
    }

    public interface OAuth2UserInfo {
        String getEmail();
        String getMemberName();
        String getOauthId();
    }

    //구글
    public static class GoogleUserInfo implements OAuth2UserInfo {
        private final Map<String, Object> attributes;

        public GoogleUserInfo(Map<String, Object> attributes) {
            this.attributes = attributes;
        }

        @Override
        public String getEmail() {
            return (String) attributes.get("email");
        }

        @Override
        public String getMemberName() {
            return (String) attributes.get("name"); // 구글은 name 필드 사용
        }

        @Override
        public String getOauthId() {
            return (String) attributes.get("sub");
        }
    }

    //네이버
    public static class NaverUserInfo implements OAuth2UserInfo {
        private final Map<String, Object> attributes;

        public NaverUserInfo(Map<String, Object> attributes) {
            this.attributes = (Map<String, Object>) attributes.get("response");
        }

        @Override
        public String getEmail() {
            return (String) attributes.get("email");
        }

        @Override
        public String getMemberName() {
            return (String) attributes.get("nickname");
        }

        @Override
        public String getOauthId() {
            return (String) attributes.get("id");
        }
    }

    //카카오
    public static class KakaoUserInfo implements OAuth2UserInfo {
        private final Map<String, Object> attributes;

        public KakaoUserInfo(Map<String, Object> attributes) {
            this.attributes = attributes;
        }

        @Override
        public String getEmail() {
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
            return (String) account.get("email");
        }

        @Override
        public String getMemberName() {
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) account.get("profile");
            return (String) profile.get("nickname");
        }

        @Override
        public String getOauthId() {
            return String.valueOf(attributes.get("id"));
        }
    }
}
