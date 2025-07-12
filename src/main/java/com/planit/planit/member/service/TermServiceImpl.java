package com.planit.planit.member.service;
import com.planit.planit.config.TermConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TermServiceImpl implements TermService {
    private final TermConfig termConfig;

    @Override
    public Map<String, Map<String, String>> getAllTermsUrls() {
        Map<String, Map<String, String>> termsInfo = new HashMap<>();

        String baseUrl = Optional.ofNullable(termConfig.getBaseUrl()).orElse("");

        Map<String, TermConfig.AgreementDetail> configuredTerms =
                Optional.ofNullable(termConfig.getTerms()).orElse(Collections.emptyMap());

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