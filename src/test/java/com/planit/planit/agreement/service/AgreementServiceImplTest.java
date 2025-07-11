package com.planit.planit.agreement.service;

import com.planit.planit.config.AgreementConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
class AgreementServiceImplTest {

    @Mock
    private AgreementConfig agreementConfig;

    @InjectMocks
    private AgreementServiceImpl agreementService; // 테스트 대상 (System Under Test)

    @Nested
    @DisplayName("getAllTermsUrls 메서드는")
    class GetAllTermsUrls {

        @Test
        @DisplayName("application.yml에 정의된 모든 약관의 URL과 버전을 올바르게 반환한다")
        void returns_all_agreement_urls_and_versions_from_config() {
            // Given:
            String mockBaseUrl = "https://testdomain.com/agreements/";
            Map<String, AgreementConfig.AgreementDetail> mockTermsMap = new HashMap<>();

            // 개인정보처리방침
            AgreementConfig.AgreementDetail privacyPolicy = new AgreementConfig.AgreementDetail();
            privacyPolicy.setVersion("20250711");
            privacyPolicy.setFileName("TermOfPrivacy_v20250711.html");
            mockTermsMap.put("term-of-privacy", privacyPolicy);

            // 서비스 이용약관
            AgreementConfig.AgreementDetail termOfUse = new AgreementConfig.AgreementDetail();
            termOfUse.setVersion("20250711");
            termOfUse.setFileName("TermOfUse_v20250711.html");
            mockTermsMap.put("term-of-use", termOfUse);

            // 정보통신망 이용촉진 및 정보보호 등에 관한 법률 관련 약관
            AgreementConfig.AgreementDetail termOfInfo = new AgreementConfig.AgreementDetail();
            termOfInfo.setVersion("20250711");
            termOfInfo.setFileName("TermOfInfo_v20250711.html");
            mockTermsMap.put("term-of-info", termOfInfo);

            // 개인정보 제3자 제공 동의
            AgreementConfig.AgreementDetail thirdPartyConsent = new AgreementConfig.AgreementDetail();
            thirdPartyConsent.setVersion("20250711");
            thirdPartyConsent.setFileName("ThirdPartyConsent_v20250711.html");
            mockTermsMap.put("third-party-ad-consent", thirdPartyConsent);

            // Mock 객체 (agreementConfig)의 메서드 호출 시 어떤 값을 반환할지 정의
            when(agreementConfig.getBaseUrl()).thenReturn(mockBaseUrl);
            when(agreementConfig.getTerms()).thenReturn(mockTermsMap);

            // When:
            Map<String, Map<String, String>> result = agreementService.getAllTermsUrls();

            // Then (검증):
            // 1. 결과 맵이 null이 아니고 비어있지 않은지 확인
            assertNotNull(result, "결과 맵은 null이 아니어야 합니다.");
            assertFalse(result.isEmpty(), "결과 맵은 비어있지 않아야 합니다.");
            // 2. 결과 맵의 크기가 예상하는 약관 개수와 일치하는지 확인
            assertEquals(mockTermsMap.size(), result.size(), "결과 맵의 약관 개수가 예상과 일치해야 합니다.");

            // 3. 각 약관별로 버전과 URL이 올바르게 생성되었는지 상세 검증
            // 개인정보처리방침 (term-of-privacy)
            assertTrue(result.containsKey("term-of-privacy"), "'term-of-privacy' 키가 결과 맵에 포함되어야 합니다.");
            Map<String, String> privacyResult = result.get("term-of-privacy");
            assertNotNull(privacyResult, "'term-of-privacy'의 상세 정보는 null이 아니어야 합니다.");
            assertEquals("20250711", privacyResult.get("version"), "'term-of-privacy'의 버전이 일치해야 합니다.");
            assertEquals(mockBaseUrl + "TermOfPrivacy_v20250711.html", privacyResult.get("url"), "'term-of-privacy'의 URL이 일치해야 합니다.");

            // 서비스 이용약관 (term-of-use)
            assertTrue(result.containsKey("term-of-use"), "'term-of-use' 키가 결과 맵에 포함되어야 합니다.");
            Map<String, String> useResult = result.get("term-of-use");
            assertNotNull(useResult, "'term-of-use'의 상세 정보는 null이 아니어야 합니다.");
            assertEquals("20250711", useResult.get("version"), "'term-of-use'의 버전이 일치해야 합니다.");
            assertEquals(mockBaseUrl + "TermOfUse_v20250711.html", useResult.get("url"), "'term-of-use'의 URL이 일치해야 합니다.");

            // 정보통신망 이용촉진 및 정보보호 등에 관한 법률 관련 약관 (term-of-info)
            assertTrue(result.containsKey("term-of-info"), "'term-of-info' 키가 결과 맵에 포함되어야 합니다.");
            Map<String, String> infoResult = result.get("term-of-info");
            assertNotNull(infoResult, "'term-of-info'의 상세 정보는 null이 아니어야 합니다.");
            assertEquals("20250711", infoResult.get("version"), "'term-of-info'의 버전이 일치해야 합니다.");
            assertEquals(mockBaseUrl + "TermOfInfo_v20250711.html", infoResult.get("url"), "'term-of-info'의 URL이 일치해야 합니다.");

            // 개인정보 제3자 제공 동의 (third-party-ad-consent)
            assertTrue(result.containsKey("third-party-ad-consent"), "'third-party-ad-consent' 키가 결과 맵에 포함되어야 합니다.");
            Map<String, String> consentResult = result.get("third-party-ad-consent");
            assertNotNull(consentResult, "'third-party-ad-consent'의 상세 정보는 null이 아니어야 합니다.");
            assertEquals("20250711", consentResult.get("version"), "'third-party-ad-consent'의 버전이 일치해야 합니다.");
            assertEquals(mockBaseUrl + "ThirdPartyConsent_v20250711.html", consentResult.get("url"), "'third-party-ad-consent'의 URL이 일치해야 합니다.");


            // 4. Mock 객체의 메서드가 예상대로 호출되었는지 확인 (선택 사항이지만 좋은 습관)
            verify(agreementConfig, times(1)).getBaseUrl();
            verify(agreementConfig, times(1)).getTerms();
            verifyNoMoreInteractions(agreementConfig); // 더 이상 다른 호출은 없었는지 확인
        }

        @Test
        @DisplayName("AgreementConfig에 약관 정보가 없을 때 빈 맵을 반환한다")
        void returns_empty_map_when_no_terms_in_config() {
            // Given (준비):
            String mockBaseUrl = "https://testdomain.com/agreements/";
            Map<String, AgreementConfig.AgreementDetail> emptyTermsMap = new HashMap<>();

            when(agreementConfig.getBaseUrl()).thenReturn(mockBaseUrl);
            when(agreementConfig.getTerms()).thenReturn(emptyTermsMap);

            // When (실행):
            Map<String, Map<String, String>> result = agreementService.getAllTermsUrls();

            // Then (검증):
            assertNotNull(result, "결과 맵은 null이 아니어야 합니다.");
            assertTrue(result.isEmpty(), "약관 정보가 없을 때 결과 맵은 비어있어야 합니다.");

            verify(agreementConfig, times(1)).getBaseUrl();
            verify(agreementConfig, times(1)).getTerms();
            verifyNoMoreInteractions(agreementConfig);
        }

        @Test
        @DisplayName("getTerms가 null을 반환할 경우 빈 맵을 반환한다")
        void returns_empty_map_when_getTerms_returns_null() {
            // Given (준비):
            String mockBaseUrl = "https://testdomain.com/agreements/";

            when(agreementConfig.getBaseUrl()).thenReturn(mockBaseUrl);
            when(agreementConfig.getTerms()).thenReturn(null); // getTerms가 null 반환

            // When (실행):
            Map<String, Map<String, String>> result = agreementService.getAllTermsUrls();

            // Then (검증):
            assertNotNull(result, "결과 맵은 null이 아니어야 합니다.");
            assertTrue(result.isEmpty(), "getTerms가 null을 반환할 때 결과 맵은 비어있어야 합니다.");

            verify(agreementConfig, times(1)).getBaseUrl();
            verify(agreementConfig, times(1)).getTerms();
            verifyNoMoreInteractions(agreementConfig);
        }

    }
}