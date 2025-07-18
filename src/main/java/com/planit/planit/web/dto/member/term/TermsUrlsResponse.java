package com.planit.planit.web.dto.member.term;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "약관 URL 목록 응답")
public class TermsUrlsResponse {
    @Schema(description = "약관별 URL 정보", example = "{ 'term-of-info': { 'version': '20250711', 'url': 'http://localhost:8080/terms/TermOfInfo.html' }  etc..... }")
    private Map<String, TermDetail> terms;

    public static TermsUrlsResponse from(Map<String, Map<String, String>> map) {
        return new TermsUrlsResponse(
            map.entrySet().stream().collect(
                java.util.stream.Collectors.toMap(
                    Map.Entry::getKey,
                    e -> new TermDetail(e.getValue().get("version"), e.getValue().get("url"))
                )
            )
        );
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "약관 상세 정보")
    public static class TermDetail {
        @Schema(description = "약관 버전", example = "20250711")
        private String version;
        @Schema(description = "약관 URL", example = "http://localhost:8080/terms/TermOfInfo.html")
        private String url;
    }
} 