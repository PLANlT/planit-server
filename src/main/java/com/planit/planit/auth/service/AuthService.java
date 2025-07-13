package com.planit.planit.auth.service;

import com.planit.planit.web.dto.auth.OAuthLoginDTO;
import com.planit.planit.web.dto.auth.TokenRefreshDTO;

public interface AuthService {

    // 로그인/회원가입/약관동의 통합 signIn 메서드
    OAuthLoginDTO.LoginResponse signIn(OAuthLoginDTO.LoginRequest loginRequest);

    void signOut(Long memberId, String accessToken);

    TokenRefreshDTO.Response refreshAccessToken(String refreshToken);

}
