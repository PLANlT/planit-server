package com.planit.planit.web.dto.auth.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class OAuthLoginDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String oauthProvider;
        private String oauthToken; //클라이언트에서 전달한 소셜 토큰 (ID Token 또는 Access Token)
    }

    @Builder
    public static class Response {

        private final String email;
        private final String name;
        private final String accessToken;
        private final String refreshToken;

        private final boolean isNewMember;
        private final boolean isSignUpCompleted;

        @JsonProperty("email")
        public String getEmail() {
            return email;
        }

        @JsonProperty("name")
        public String getName() {
            return name;
        }

        @JsonProperty("accessToken")
        public String getAccessToken() {
            return accessToken;
        }

        @JsonProperty("refreshToken")
        public String getRefreshToken() {
            return refreshToken;
        }

        @JsonProperty("isNewMember")
        public boolean isNewMember() {
            return isNewMember;
        }

        @JsonProperty("isSignUpCompleted")
        public boolean isSignUpCompleted() {
            return isSignUpCompleted;
        }
    }
}