package com.planit.planit.web.dto.member.fcmtoken;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FcmTokenDTO {

    @Getter
    public static class SaveRequest {
        private String token;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private String token;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeleteRequest {
        private String token;
    }
}
