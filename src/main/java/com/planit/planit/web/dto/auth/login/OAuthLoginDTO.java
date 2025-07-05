package com.planit.planit.web.dto.auth.login;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

public class OAuthLoginDTO {

    @Getter
    @Builder
    public static class Request {
        private String oauthProvider;
        private String oauthAccessToken;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private java.time.LocalDateTime termOfUse;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private java.time.LocalDateTime termOfPrivacy;
    }

    @Getter
    @Builder
    public static class Response {
        private String email;
        private String name;
        private String accessToken;
        private String refreshToken;
        @JsonProperty("isNewMember")
        private boolean isNewMember;
    }
}
