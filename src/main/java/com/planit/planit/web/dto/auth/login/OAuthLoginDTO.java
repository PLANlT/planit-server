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

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        private String email;
        private String name;
        private String accessToken;
        private String refreshToken;
        @JsonProperty("isNewMember")
        private boolean isNewMember;
        @JsonProperty("isSignUpCompleted")
        private boolean isSignUpCompleted;
    }
}
