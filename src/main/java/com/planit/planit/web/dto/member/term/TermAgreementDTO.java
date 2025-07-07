package com.planit.planit.web.dto.member.term;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

public class TermAgreementDTO {


    @Data // => Getter + Setter + EqualsAndHashCode + toString + RequiredArgsConstructor
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        // 만약 이 필드들이 LocalDateTime 타입이라면 아래처럼 설정
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
        private LocalDateTime overFourteen; // 이 필드도 LocalDateTime이라면
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
