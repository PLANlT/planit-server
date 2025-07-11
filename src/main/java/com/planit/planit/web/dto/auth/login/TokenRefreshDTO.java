package com.planit.planit.web.dto.auth.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TokenRefreshDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private String refreshToken;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String accessToken;
        private String refreshToken; // rotation 시에만 갱신
    }
}
