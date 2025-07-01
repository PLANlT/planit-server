package com.planit.planit.web.dto.member.term;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class TermAgreementDTO {

    @Getter
    @Builder
    public static class Request {
        private LocalDateTime termOfUse;
        private LocalDateTime termOfPrivacy;
    }

    @Getter
    @Builder
    public static class Response {
        private String email;
        private LocalDateTime termOfUse;
        private LocalDateTime termOfPrivacy;
    }
}
