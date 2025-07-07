package com.planit.planit.member.service;

import com.planit.planit.config.oauth.CustomOAuth2User;
import com.planit.planit.web.dto.auth.login.OAuthLoginDTO;
import com.planit.planit.web.dto.member.term.TermAgreementDTO;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

    // 로그인/회원가입/약관동의 통합 signIn 메서드
    OAuthLoginDTO.Response signIn(OAuthLoginDTO.Request request);

    void signOut(Long memberId, String accessToken);

    OAuthLoginDTO.Response signInWithIdToken(OAuthLoginDTO.Request request);
}
