package com.planit.planit.member.service;
import com.planit.planit.member.association.TermInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TermServiceImpl implements TermService {

    private final TermInfo termInfo;
    private final ResourceLoader resourceLoader;

    @Override
    public Map<String, Map<String, String>> getAllTermsUrls() {
        Map<String, Map<String, String>> termInfos = new HashMap<>();

        String baseUrl = Optional.ofNullable(termInfo.getBaseUrl()).orElse("");

        Map<String, TermInfo.TermDetail> configuredTerms =
                Optional.ofNullable(termInfo.getTerms()).orElse(Collections.emptyMap());

        configuredTerms.forEach((key, termDetail) -> {
            Map<String, String> detailMap = new HashMap<>();
            detailMap.put("version", termDetail.getVersion());
            String url = termDetail.getFullUrl(baseUrl);
            // 파일 존재 여부 체크
            Resource resource = resourceLoader.getResource("classpath:/static/terms/" + termDetail.getFileName());
            if (!resource.exists()) {
                throw new com.planit.planit.common.api.member.MemberHandler(
                    com.planit.planit.common.api.member.status.MemberErrorStatus.TERM_FILE_NOT_FOUND);
            }
            detailMap.put("url", url);
            termInfos.put(key, detailMap);
        });

        return termInfos;
    }
}