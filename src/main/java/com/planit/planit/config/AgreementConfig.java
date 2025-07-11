package com.planit.planit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "app.agreement")
public class AgreementConfig {
    private String baseUrl;
    private Map<String, AgreementDetail> terms;

    @Getter
    @Setter
    public static class AgreementDetail {  private String version;
        private String fileName;   // 실제 HTML 파일명 (예: "TermOfPrivacy_v20250711.html")

        public String getFullUrl(String baseUrl) {
            // baseUrl이 '/'로 끝나지 않으면 '/'를 추가하여 올바른 URL 경로를 만듭니다.
            if (baseUrl != null && !baseUrl.endsWith("/")) {
                return baseUrl + "/" + fileName;
            }
            return baseUrl + fileName;
        }
    }
}
