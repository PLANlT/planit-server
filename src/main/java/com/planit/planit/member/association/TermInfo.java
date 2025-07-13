package com.planit.planit.member.association;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "app.agreement")
public class TermInfo {
    private String baseUrl;
    private Map<String, TermDetail> terms;

    @Getter
    @Setter
    public static class TermDetail {
        private String version;
        private String fileName;   // 실제 HTML 파일명 (예: "TermOfPrivacy.html")

        public String getFullUrl(String baseUrl) {
            // baseUrl이 '/'로 끝나지 않으면 '/'를 추가하여 올바른 URL 경로를 만듭니다.
            if (baseUrl != null && !baseUrl.endsWith("/")) {
                return baseUrl + "/" + fileName;
            }
            return baseUrl + fileName;
        }
    }
}
