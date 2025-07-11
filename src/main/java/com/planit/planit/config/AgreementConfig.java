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
        private String fileName;   /****
         * Constructs the full URL to the agreement file by combining the provided base URL and the file name.
         *
         * If the base URL does not end with a slash, a slash is inserted before appending the file name.
         *
         * @param baseUrl the base URL to which the file name will be appended
         * @return the complete URL to the agreement file
         */

        public String getFullUrl(String baseUrl) {
            // baseUrl이 '/'로 끝나지 않으면 '/'를 추가하여 올바른 URL 경로를 만듭니다.
            if (baseUrl != null && !baseUrl.endsWith("/")) {
                return baseUrl + "/" + fileName;
            }
            return baseUrl + fileName;
        }
    }
}
