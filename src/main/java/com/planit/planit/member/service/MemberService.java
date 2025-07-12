package com.planit.planit.member.service;

import com.planit.planit.web.dto.auth.login.OAuthLoginDTO;
import com.planit.planit.web.dto.auth.login.TokenRefreshDTO;
import com.planit.planit.web.dto.member.MemberInfoResponseDTO;
import com.planit.planit.web.dto.member.MemberResponseDTO;
import com.planit.planit.web.dto.member.term.TermAgreementDTO;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

    // 로그인/회원가입/약관동의 통합 signIn 메서드
    OAuthLoginDTO.LoginResponse signIn(OAuthLoginDTO.LoginRequest loginRequest);

    void signOut(Long memberId, String accessToken);

    MemberResponseDTO.ConsecutiveDaysDTO getConsecutiveDays(Long memberId);

    void completeTermsAgreement(Long id, TermAgreementDTO.Request request);


    TokenRefreshDTO.Response refreshAccessToken(String refreshToken);

    MemberInfoResponseDTO getMemberInfo(Long memberId);
}
