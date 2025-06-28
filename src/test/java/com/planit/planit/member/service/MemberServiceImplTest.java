//package com.planit.planit.member.service;
//
//import com.planit.planit.auth.FakeOAuth2User;
//import com.planit.planit.config.jwt.JwtProvider;
//import com.planit.planit.member.Member;
//import com.planit.planit.member.MemberRepository;
//import com.planit.planit.member.enums.Role;
//import com.planit.planit.member.enums.SignType;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Map;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//
//@ExtendWith(MockitoExtension.class)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@Transactional
//class MemberServiceImplTest {
//
//    @InjectMocks
//    private MemberServiceImpl memberServiceImpl;
//
//    @Mock
//    private MemberRepository memberRepository;
//
//    @Mock
//    private JwtProvider jwtProvider;
//
//    @Test
//    @Order(1)
//    @Transactional
//    @DisplayName("신규 회원이면 회원가입 처리되고 isNewMember=false 를 반환한다 (실패)")
//    void register_isNewMember_false_return() {
//        // given
//        Map<String, Object> attributes = Map.of(
//                "email", "newbie@gmail.com",
//                "name", "뉴비",
//                "picture", "https://image.url"
//        );
//
//        OAuth2User oAuth2User = new FakeOAuth2User(attributes);
//
//        given(memberRepository.findByEmail("newbie@gmail.com"))
//                .willReturn(Optional.empty());
//
//        given(memberRepository.save(any()))
//                .willAnswer(invocation -> invocation.getArgument(0));
//
//        given(jwtProvider.createAccessToken(any(), any(), any(), any()))
//                .willReturn("access-token");
//
//        given(jwtProvider.createRefreshToken(any(), any(), any(), any()))
//                .willReturn("refresh-token");
//
//         //when
//         OAuthLoginResponse response = memberServiceImpl.oauthRegister(oAuth2User, SignType.GOOGLE);
//
//         //then
//         assertThat(response.isNewMember()).isFalse();
//         assertThat(response.getEmail()).isEqualTo("newbie@gmail.com");
//         assertThat(response.getAccessToken()).isEqualTo("access-token");
//        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
//    }
//
//    @Test
//    @Order(2)
//    @Transactional
//    @DisplayName("기존 회원이면 isNewMember가 true로 반환된다 (실패)")
//    void if_existingMember_isNewMember_true() {
//        // given
//        OAuth2User user = new FakeOAuth2User(Map.of(
//                "email", "existing@planit.com",
//                "name", "플래닛유저"
//        ));
//        Member existingMember = Member.builder()
//                .email("existing@planit.com")
//                .memberName("플래닛유저")
//                .role(Role.USER)
//                .build();
//        given(memberRepository.findByEmail("existing@planit.com")).willReturn(Optional.of(existingMember));
//        given(jwtProvider.createAccessToken(any(), any(), any(), any())).willReturn("access-token");
//        given(jwtProvider.createRefreshToken(any(), any(), any(), any())).willReturn("refresh-token");
//
//        // when
//        OAuthLoginResponse response = memberServiceImpl.oauthRegister(user, SignType.GOOGLE);
//
//        // then
//        assertThat(response.isNewMember()).isTrue(); // 기존 회원이므로 true
//    }
//}
