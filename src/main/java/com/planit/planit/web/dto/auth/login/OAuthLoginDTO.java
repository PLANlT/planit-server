package com.planit.planit.web.dto.auth.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class OAuthLoginDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "OAuth 로그인 요청 DTO")
    public static class Request {
        @Schema(description = "소셜 로그인 제공자 (KAKAO, NAVER, GOOGLE)", example = "KAKAO")
        private String oauthProvider;

        @Schema(description = "클라이언트로부터 받은 OAuth ID Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
        private String oauthToken;
    }

    @Builder
    public static class Response {

        @Schema(description = "회원 고유 ID")
        private Long id;
        @Schema(description = "회원 이메일")
        private String email;
        @Schema(description = "회원 이름")
        private String name;
        @Schema(description = "서버에서 발급한 Access Token")
        private String accessToken;
        @Schema(description = "서버에서 발급한 Refresh Token")
        private String refreshToken;
        @Schema(description = "새로 가입한 회원인지 여부")
        private boolean isNewMember;
        @Schema(description = "약관 동의 완료 여부")
        private boolean isSignUpCompleted;


        @JsonProperty("id")
        public Long getId() {
            return id;
        }

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