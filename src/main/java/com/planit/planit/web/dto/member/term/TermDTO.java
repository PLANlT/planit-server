package com.planit.planit.web.dto.member.term;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

public class TermDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AgreementRequest {

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(type = "string", example = "2025-07-08T01:17:17") // 원하는 형식의 예시 값
        private LocalDateTime termOfUse;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(type = "string", example = "2025-07-08T01:17:17")
        private LocalDateTime termOfPrivacy;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(type = "string", example = "2025-07-08T01:17:17")
        private LocalDateTime termOfInfo;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(type = "string", example = "2025-07-08T01:17:17")
        private LocalDateTime thirdPartyAdConsent;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(type = "string", example = "2025-07-08T01:17:17")
        private LocalDateTime overFourteen;
    }
}
