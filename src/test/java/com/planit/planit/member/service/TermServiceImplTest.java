package com.planit.planit.member.service;

import com.planit.planit.member.association.TermInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
class TermServiceImplTest {

    @Mock
    private TermInfo termInfo;

    @Mock
    private ResourceLoader resourceLoader;

    @InjectMocks
    private TermServiceImpl agreementService; // 테스트 대상 (System Under Test)

    @Nested
    @DisplayName("getAllTermsUrls 메서드는")
    class GetAllTermsUrls {

        @Test
        @DisplayName("application.yml에 정의된 모든 약관의 URL과 버전을 올바르게 반환한다")
        void returns_all_agreement_urls_and_versions_from_config() {
            // Given:
            String mockBaseUrl = "https://testdomain.com/agreements/";
            Map<String, TermInfo.TermDetail> mockTermsMap = new HashMap<>();

            // 정보통신망 이용촉진 및 정보보호 등에 관한 법률 관련 약관
            TermInfo.TermDetail termOfInfo = new TermInfo.TermDetail();
            termOfInfo.setVersion("20250711");
            termOfInfo.setFileName("TermOfInfo.html");
            mockTermsMap.put("term-of-info", termOfInfo);

            // 개인정보 수집 및 이용 동의서
            TermInfo.TermDetail privacyPolicy = new TermInfo.TermDetail();
            privacyPolicy.setVersion("20250711");
            privacyPolicy.setFileName("TermOfPrivacy.html");
            mockTermsMap.put("term-of-privacy", privacyPolicy);

            // 서비스 이용약관
            TermInfo.TermDetail termOfUse = new TermInfo.TermDetail();
            termOfUse.setVersion("20250711");
            termOfUse.setFileName("TermOfUse.html");
            mockTermsMap.put("term-of-use", termOfUse);

            // 개인정보 제3자 제공 동의
            TermInfo.TermDetail thirdPartyConsent = new TermInfo.TermDetail();
            thirdPartyConsent.setVersion("20250711");
            thirdPartyConsent.setFileName("ThirdPartyConsent.html");
            mockTermsMap.put("third-party-ad-consent", thirdPartyConsent);

            // 계정 삭제 창구
            TermInfo.TermDetail planitAccountDeletion = new TermInfo.TermDetail();
            planitAccountDeletion.setVersion("20250711");
            planitAccountDeletion.setFileName("PlanitAccountDeletion.html");
            mockTermsMap.put("planit-account-deletion", planitAccountDeletion);

            // Mock 객체 (agreementConfig)의 메서드 호출 시 어떤 값을 반환할지 정의
            when(termInfo.getBaseUrl()).thenReturn(mockBaseUrl);
            when(termInfo.getTerms()).thenReturn(mockTermsMap);
            when(resourceLoader.getResource(any())).thenReturn(new PathResource(""));

            // Mock resourceLoader 동작 추가
            Resource mockResource = mock(Resource.class);
            when(resourceLoader.getResource(anyString())).thenReturn(mockResource);
            when(mockResource.exists()).thenReturn(true);

            // When:
            Map<String, Map<String, String>> result = agreementService.getAllTermsUrls();

            // Then (검증):
            // 1. 결과 맵이 null이 아니고 비어있지 않은지 확인
            assertNotNull(result, "결과 맵은 null이 아니어야 합니다.");
            assertFalse(result.isEmpty(), "결과 맵은 비어있지 않아야 합니다.");
            // 2. 결과 맵의 크기가 예상하는 약관 개수와 일치하는지 확인
            assertEquals(mockTermsMap.size(), result.size(), "결과 맵의 약관 개수가 예상과 일치해야 합니다.");

            // 3. 각 약관별로 버전과 URL이 올바르게 생성되었는지 상세 검증
            // 정보통신망 이용촉진 및 정보보호 등에 관한 법률 관련 약관 (term-of-info)
            assertTrue(result.containsKey("term-of-info"), "'term-of-info' 키가 결과 맵에 포함되어야 합니다.");
            Map<String, String> infoResult = result.get("term-of-info");
            assertNotNull(infoResult, "'term-of-info'의 상세 정보는 null이 아니어야 합니다.");
            assertEquals("20250711", infoResult.get("version"), "'term-of-info'의 버전이 일치해야 합니다.");
            assertEquals(mockBaseUrl + "terms/" + "TermOfInfo.html", infoResult.get("url"), "'term-of-info'의 URL이 일치해야 합니다.");

            // 개인정보 수집 및 이용 동의서 (term-of-privacy)
            assertTrue(result.containsKey("term-of-privacy"), "'term-of-privacy' 키가 결과 맵에 포함되어야 합니다.");
            Map<String, String> privacyResult = result.get("term-of-privacy");
            assertNotNull(privacyResult, "'term-of-privacy'의 상세 정보는 null이 아니어야 합니다.");
            assertEquals("20250711", privacyResult.get("version"), "'term-of-privacy'의 버전이 일치해야 합니다.");
            assertEquals(mockBaseUrl + "terms/" + "TermOfPrivacy.html", privacyResult.get("url"), "'term-of-privacy'의 URL이 일치해야 합니다.");

            // 서비스 이용약관 (term-of-use)
            assertTrue(result.containsKey("term-of-use"), "'term-of-use' 키가 결과 맵에 포함되어야 합니다.");
            Map<String, String> useResult = result.get("term-of-use");
            assertNotNull(useResult, "'term-of-use'의 상세 정보는 null이 아니어야 합니다.");
            assertEquals("20250711", useResult.get("version"), "'term-of-use'의 버전이 일치해야 합니다.");
            assertEquals(mockBaseUrl + "terms/" + "TermOfUse.html", useResult.get("url"), "'term-of-use'의 URL이 일치해야 합니다.");

            // 개인정보 제3자 제공 동의 (third-party-ad-consent)
            assertTrue(result.containsKey("third-party-ad-consent"), "'third-party-ad-consent' 키가 결과 맵에 포함되어야 합니다.");
            Map<String, String> consentResult = result.get("third-party-ad-consent");
            assertNotNull(consentResult, "'third-party-ad-consent'의 상세 정보는 null이 아니어야 합니다.");
            assertEquals("20250711", consentResult.get("version"), "'third-party-ad-consent'의 버전이 일치해야 합니다.");
            assertEquals(mockBaseUrl + "terms/" + "ThirdPartyConsent.html", consentResult.get("url"), "'third-party-ad-consent'의 URL이 일치해야 합니다.");

            // 계정 삭제 창구 (planit-account-deletion)
            assertTrue(result.containsKey("planit-account-deletion"), "'planit-account-deletion' 키가 결과 맵에 포함되어야 합니다.");
            Map<String, String> deletionResult = result.get("planit-account-deletion");
            assertNotNull(deletionResult, "'planit-account-deletion'의 상세 정보는 null이 아니어야 합니다.");
            assertEquals("20250711", deletionResult.get("version"), "'planit-account-deletion'의 버전이 일치해야 합니다.");
            assertEquals(mockBaseUrl + "terms/" + "PlanitAccountDeletion.html", deletionResult.get("url"), "'planit-account-deletion'의 URL이 일치해야 합니다.");

            // 4. Mock 객체의 메서드가 예상대로 호출되었는지 확인 (선택 사항이지만 좋은 습관)
            verify(termInfo, times(1)).getBaseUrl();
            verify(termInfo, times(1)).getTerms();
            verifyNoMoreInteractions(termInfo); // 더 이상 다른 호출은 없었는지 확인
        }

        @Test
        @DisplayName("AgreementConfig에 약관 정보가 없을 때 빈 맵을 반환한다")
        void returns_empty_map_when_no_terms_in_config() {
            // Given (준비):
            String mockBaseUrl = "https://testdomain.com/agreements/";
            Map<String, TermInfo.TermDetail> emptyTermsMap = new HashMap<>();

            when(termInfo.getBaseUrl()).thenReturn(mockBaseUrl);
            when(termInfo.getTerms()).thenReturn(emptyTermsMap);

            // When (실행):
            Map<String, Map<String, String>> result = agreementService.getAllTermsUrls();

            // Then (검증):
            assertNotNull(result, "결과 맵은 null이 아니어야 합니다.");
            assertTrue(result.isEmpty(), "약관 정보가 없을 때 결과 맵은 비어있어야 합니다.");

            verify(termInfo, times(1)).getBaseUrl();
            verify(termInfo, times(1)).getTerms();
            verifyNoMoreInteractions(termInfo);
        }

        @Test
        @DisplayName("getTerms가 null을 반환할 경우 빈 맵을 반환한다")
        void returns_empty_map_when_getTerms_returns_null() {
            // Given (준비):
            String mockBaseUrl = "https://testdomain.com/agreements/";

            when(termInfo.getBaseUrl()).thenReturn(mockBaseUrl);
            when(termInfo.getTerms()).thenReturn(null); // getTerms가 null 반환

            // When (실행):
            Map<String, Map<String, String>> result = agreementService.getAllTermsUrls();

            // Then (검증):
            assertNotNull(result, "결과 맵은 null이 아니어야 합니다.");
            assertTrue(result.isEmpty(), "getTerms가 null을 반환할 때 결과 맵은 비어있어야 합니다.");

            verify(termInfo, times(1)).getBaseUrl();
            verify(termInfo, times(1)).getTerms();
            verifyNoMoreInteractions(termInfo);
        }

    }
}