package com.planit.planit.web.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TokenRefreshDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "토큰 리프레시 요청 DTO")
    public static class Request {
        @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        private String refreshToken;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "토큰 리프레시 응답 DTO")
    public static class Response {
        @Schema(description = "새로운 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        private String accessToken;
        
        @Schema(description = "리프레시 토큰 (rotation 시에만 갱신)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        private String refreshToken; // rotation 시에만 갱신
        
        @Schema(description = "회원 고유 ID")
        private Long id;
        
        @Schema(description = "회원 이메일")
        private String email;
        
        @Schema(description = "회원 이름")
        private String name;
        
        @Schema(description = "회원 권한")
        private String role;
    }
}
