package com.planit.planit.web.dto.member.term;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

public class TermAgreementDTO {


    @Data // => Getter + Setter + EqualsAndHashCode + toString + RequiredArgsConstructor
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime termOfUse;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime termOfPrivacy;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime termOfInfo;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime overFourteen;
    }

    @Getter
    @Builder
    public static class Response {
        private String email;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime termOfUse;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime termOfPrivacy;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime termOfInfo;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime overFourteen;
    }
}
