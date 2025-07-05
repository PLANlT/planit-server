package com.planit.planit.web.dto.member.term;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class TermAgreementDTO {

    @Getter
    @Builder
    public static class Request {
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime termOfUse;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime termOfPrivacy;
    }

    @Getter
    @Builder
    public static class Response {
        private String email;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime termOfUse;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime termOfPrivacy;
    }
}
