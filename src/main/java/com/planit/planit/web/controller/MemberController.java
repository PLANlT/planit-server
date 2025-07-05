package com.planit.planit.web.controller;


import com.planit.planit.common.api.ApiResponse;
import com.planit.planit.common.api.member.status.MemberSuccessStatus;
import com.planit.planit.config.jwt.UserPrincipal;
import com.planit.planit.config.oauth.CustomOAuth2UserService;
import com.planit.planit.member.service.MemberService;
import com.planit.planit.web.dto.auth.login.OAuthLoginDTO;
import com.planit.planit.config.oauth.CustomOAuth2User;
import com.planit.planit.web.dto.member.term.TermAgreementDTO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "Member", description = "회원 관련 API")
public class MemberController {
    private final MemberService memberService;


    @Operation(summary = "OAuth 로그인/회원가입", description = "OAuth 인증 후 로그인 또는 회원가입을 처리합니다.")
    @PostMapping("/sign-in")
    public ApiResponse<OAuthLoginDTO.Response> signIn(
            @RequestAttribute(name = "oauthUser") CustomOAuth2User oAuth2User,
            @RequestBody(required = false) TermAgreementDTO.Request termRequest
    ) {
        OAuthLoginDTO.Response response = memberService.signIn(oAuth2User, termRequest);
        
        if (response.isNewMember() && response.isSignUpCompleted()) {
            // 신규 회원이 회원가입을 완료한 경우
            return ApiResponse.onSuccess(MemberSuccessStatus.SIGN_UP_SUCCESS, response);
        } else if (response.isNewMember()) {
            // 신규 회원이지만 약관 동의가 필요한 경우
            return ApiResponse.onSuccess(MemberSuccessStatus.SIGN_UP_SUCCESS, response);
        } else {
            // 기존 회원 로그인
            return ApiResponse.onSuccess(MemberSuccessStatus.SIGN_IN_SUCCESS, response);
        }
    }

    @Operation(summary = "로그아웃", description = "사용자 로그아웃을 처리하고 토큰을 블랙리스트에 추가합니다.")
    @SecurityRequirement(name = "accessToken")
    @PostMapping("/sign-out")
    public ApiResponse<Void> signOut(@AuthenticationPrincipal UserPrincipal principal, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring("Bearer ".length());
            memberService.signOut(principal.getId(), accessToken);
        }
        return ApiResponse.onSuccess(MemberSuccessStatus.SIGN_OUT_SUCCESS, null);
    }
}
