package com.planit.planit.agreement.service;
import com.planit.planit.config.AgreementConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AgreementServiceImpl implements AgreementService {
    private final AgreementConfig agreementConfig;

    /**
     * Retrieves a map of all agreement terms, each containing its version and a fully constructed URL.
     *
     * @return a map where each key is a term identifier and the value is a map with "version" and "url" entries for that term
     */
    @Override
    public Map<String, Map<String, String>> getAllTermsUrls() {
        Map<String, Map<String, String>> termsInfo = new HashMap<>();

        String baseUrl = Optional.ofNullable(agreementConfig.getBaseUrl()).orElse("");

        Map<String, AgreementConfig.AgreementDetail> configuredTerms =
                Optional.ofNullable(agreementConfig.getTerms()).orElse(Collections.emptyMap());

        configuredTerms.forEach((key, agreementDetail) -> {
            Map<String, String> detailMap = new HashMap<>();
            detailMap.put("version", agreementDetail.getVersion());
            // AgreementDetail 내부의 getFullUrl 메서드를 사용
            detailMap.put("url", agreementDetail.getFullUrl(baseUrl));
            termsInfo.put(key, detailMap);
        });

        return termsInfo;
    }
}