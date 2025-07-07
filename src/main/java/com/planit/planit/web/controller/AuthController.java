package com.planit.planit.web.controller;

import com.planit.planit.common.api.ApiResponse;
import com.planit.planit.common.api.general.status.SuccessResponse;
import com.planit.planit.common.api.general.status.SuccessStatus;
import com.planit.planit.member.service.MemberService;
import com.planit.planit.web.dto.auth.login.OAuthLoginDTO;
import com.planit.planit.web.dto.auth.login.TokenRefreshDTO;
import com.sun.net.httpserver.Authenticator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    @PostMapping("/refresh")
    public ApiResponse<TokenRefreshDTO.Response> refreshToken(@RequestBody TokenRefreshDTO.Request request) {
        TokenRefreshDTO.Response response = memberService.refreshAccessToken(request.getRefreshToken());
        return ApiResponse.onSuccess(SuccessStatus.REFRESH_SUCCESS, response);
    }
}
