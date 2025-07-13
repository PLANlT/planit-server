package com.planit.planit.member.service;
import com.planit.planit.member.association.TermInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TermServiceImpl implements TermService {

    private final TermInfo termInfo;

    @Override
    public Map<String, Map<String, String>> getAllTermsUrls() {
        Map<String, Map<String, String>> termInfos = new HashMap<>();

        String baseUrl = Optional.ofNullable(termInfo.getBaseUrl()).orElse("");

        Map<String, TermInfo.TermDetail> configuredTerms =
                Optional.ofNullable(termInfo.getTerms()).orElse(Collections.emptyMap());

        configuredTerms.forEach((key, termDetail) -> {
            Map<String, String> detailMap = new HashMap<>();
            detailMap.put("version", termDetail.getVersion());
            // TermDetail 내부의 getFullUrl 메서드를 사용
            detailMap.put("url", termDetail.getFullUrl(baseUrl));
            termInfos.put(key, detailMap);
        });

        return termInfos;
    }
}