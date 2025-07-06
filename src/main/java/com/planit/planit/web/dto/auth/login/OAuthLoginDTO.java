package com.planit.planit.web.dto.auth.login;

import lombok.Builder;
import lombok.Getter;

public class OAuthLoginDTO {

    @Getter
    @Builder
    public static class Request {
        private final String oauthProvider;
        private final String oauthAccessToken;
    }

    @Getter
    @Builder
    public static class Response {
        private final String email;
        private final String name;
        private final String accessToken;
        private final String refreshToken;
        private final boolean isNewMember;
    }
}
