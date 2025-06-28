package com.planit.planit.config.oauth;

import com.planit.planit.auth.oauth.OAuth2UserInfoFactory;
import com.planit.planit.member.Member;
import com.planit.planit.member.MemberRepository;
import com.planit.planit.member.enums.Role;
import com.planit.planit.member.enums.SignType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase(); // e.g. "GOOGLE"
        SignType signType = SignType.valueOf(registrationId);
        OAuth2UserInfoFactory.OAuth2UserInfo userInfo = OAuth2UserInfoFactory.of(signType, oAuth2User.getAttributes());

        String email = userInfo.getEmail();
        String oauthId = userInfo.getOauthId();
        String memberName = userInfo.getMemberName();

        return new CustomOAuth2User(
                oAuth2User,
                signType,
                null,
                email,
                null,
                memberName
        );
    }
}