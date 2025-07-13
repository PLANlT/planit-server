package com.planit.planit.web.dto.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.planit.planit.member.Member;
import com.planit.planit.member.association.SignedMember;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class OAuthLoginDTO {

    @Getter
    @Builder
    @Schema(description = "OAuth 로그인 요청 DTO")
    public static class LoginRequest {
        @Schema(description = "소셜 로그인 제공자 (KAKAO, NAVER, GOOGLE)", example = "KAKAO")
        private final String oauthProvider;

        @Schema(description = "클라이언트로부터 받은 OAuth ID Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
        private final String oauthToken;

        @JsonCreator
        public LoginRequest(
                @JsonProperty("oauthProvider") String oauthProvider,
                @JsonProperty("oauthToken") String oauthToken
        ) {
            this.oauthProvider = oauthProvider;
            this.oauthToken = oauthToken;
        }
    }

    @Getter
    @Builder
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LoginResponse {

        @Schema(description = "회원 고유 ID")
        private final Long id;
        @Schema(description = "회원 이메일")
        private final String email;
        @Schema(description = "회원 이름")
        private final String name;
        @Schema(description = "서버에서 발급한 Access Token")
        private final String accessToken;
        @Schema(description = "서버에서 발급한 Refresh Token")
        private final String refreshToken;
        @Schema(description = "새로 가입한 회원인지 여부")
        private final boolean isNewMember;
        @Schema(description = "약관 동의 완료 여부")
        private final boolean isSignUpCompleted;

        public static LoginResponse of(
                SignedMember signedMember, String accessToken, String refreshToken
        ) {
            return LoginResponse.builder()
                    .id(signedMember.getId())
                    .email(signedMember.getEmail())
                    .name(signedMember.getName())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .isNewMember(signedMember.getIsNewMember())
                    .isSignUpCompleted(!signedMember.getIsNewMember())  // 신규 회원인 경우 약관 동의 필요
                    .build();
        }
    }
}