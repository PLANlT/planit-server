
package com.planit.planit.member.service;

import com.planit.planit.config.jwt.JwtProvider;
import com.planit.planit.config.oauth.CustomOAuth2User;
import com.planit.planit.member.Member;
import com.planit.planit.member.MemberRepository;
import com.planit.planit.member.enums.Role;
import com.planit.planit.member.enums.SignType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class MemberServiceImplTest {

    @InjectMocks
    private MemberServiceImpl memberServiceImpl;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtProvider jwtProvider;


    // 각 테스트 메서드 실행 후 목 객체를 초기화
    @AfterEach
    void tearDown() {
        Mockito.reset(memberRepository, jwtProvider); // 사용된 모든 @Mock 객체를 reset
    }

    @Test
    @Order(1)
    @DisplayName("신규 회원이면 회원가입 처리되고 isNewMember = true 를 반환한다-성공")
    void register_newMember_returnsTrue() {
    // given
        String testEmail = "newbie@gmail.com";
        String testName = "뉴비";
        SignType testSignType = SignType.GOOGLE;

        // Mockito를 사용하여 OAuth2User 인터페이스의 목 객체 생성
        OAuth2User mockDelegate = mock(OAuth2User.class);

        // 목 객체의 메서드 호출 시 반환될 값 정의
        given(mockDelegate.getAttributes()).willReturn(Map.of("email", testEmail, "name", testName));

        // CustomOAuth2User 생성자에 필요한 모든 인자를 제공
        CustomOAuth2User user = new CustomOAuth2User(
                mockDelegate,
                testSignType,
                null,
                testEmail,
                Role.USER,
                testName
        );


        given(memberRepository.findByEmail("newbie@gmail.com"))
                .willReturn(Optional.empty());

        // when
        var response = memberServiceImpl.checkOAuthMember(user);

        // then
        assertThat(response.isNewMember()).isTrue();
        assertThat(response.getEmail()).isEqualTo("newbie@gmail.com");

    }

    @Test
    @Order(2)
    @DisplayName("기존 회원이면 isNewMember = false 를 반환한다-성공")
    void register_existingMember_returnsFalse() {
// given
        String testEmail = "exist@planit.com";
        String testName = "플래닛";
        SignType testSignType = SignType.GOOGLE;

        // Mockito를 사용하여 OAuth2User 인터페이스의 목 객체 생성
        OAuth2User mockDelegate = mock(OAuth2User.class);

        // 목 객체의 메서드 호출 시 반환될 값 정의
        given(mockDelegate.getAttributes()).willReturn(Map.of("email", testEmail, "name", testName));

        // CustomOAuth2User 생성자에 필요한 모든 인자를 제공
        CustomOAuth2User user = new CustomOAuth2User(
                mockDelegate,
                testSignType,
                null,
                testEmail,
                Role.USER,
                testName
        );

        var existing = Member.builder()
                .email("exist@planit.com")
                .memberName("플래닛")
                .role(Role.USER)
                .signType(SignType.GOOGLE)
                .guiltyFreeMode(false)
                .build();

        given(memberRepository.findByEmail("exist@planit.com"))
                .willReturn(Optional.of(existing));

        given(jwtProvider.createAccessToken(any(), any(), any(), any()))
                .willReturn("access-token");

        given(jwtProvider.createRefreshToken(any(), any(), any(), any()))
                .willReturn("refresh-token");

        // when
        var response = memberServiceImpl.checkOAuthMember(user);

        // then
        assertThat(response.isNewMember()).isFalse();
        assertThat(response.getEmail()).isEqualTo("exist@planit.com");
        verify(jwtProvider).createAccessToken(any(), any(), any(), any());
        verify(jwtProvider).createRefreshToken(any(), any(), any(), any());
    }
}

