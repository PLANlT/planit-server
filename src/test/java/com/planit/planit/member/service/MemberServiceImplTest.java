
package com.planit.planit.member.service;

import com.planit.planit.auth.FakeCustomOAuth2User;
import com.planit.planit.auth.FakeOAuth2User;
import com.planit.planit.config.jwt.JwtProvider;
import com.planit.planit.member.Member;
import com.planit.planit.member.MemberRepository;
import com.planit.planit.member.enums.Role;
import com.planit.planit.member.enums.SignType;
import com.planit.planit.web.dto.auth.login.converter.OAuthLoginDTO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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

    @Test
    @Order(1)
    @DisplayName("신규 회원이면 회원가입 처리되고 isNewMember = true 를 반환한다-성공")
    void register_newMember_returnsTrue() {
        // given
        Map<String, Object> attributes = Map.of(
                "email", "newbie@gmail.com",
                "name", "뉴비"
        );
        FakeCustomOAuth2User user = new FakeCustomOAuth2User(attributes, SignType.GOOGLE);

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
        Map<String, Object> attributes = Map.of(
                "email", "exist@planit.com",
                "name", "플래닛"
        );
        var user = new FakeCustomOAuth2User(attributes, SignType.GOOGLE);

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

