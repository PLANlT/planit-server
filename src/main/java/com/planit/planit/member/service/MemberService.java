package com.planit.planit.member.service;

import com.planit.planit.config.oauth.CustomOAuth2User;
import com.planit.planit.member.MemberRepository;
import com.planit.planit.member.enums.SignType;
import com.planit.planit.web.dto.auth.login.converter.OAuthLoginDTO;
import com.planit.planit.web.dto.member.term.TermAgreementDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

    //로그인인지 회원가입인지 감지
    OAuthLoginDTO.Response checkOAuthMember(CustomOAuth2User oAuth2User);

    OAuthLoginDTO.Response registerOAuthMember(CustomOAuth2User oAuth2User, TermAgreementDTO.Request request);

    void signOut(Object any);
}
