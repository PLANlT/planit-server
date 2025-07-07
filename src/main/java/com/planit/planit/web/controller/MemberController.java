package com.planit.planit.web.controller;


import com.planit.planit.common.api.ApiResponse;
import com.planit.planit.common.api.member.status.MemberSuccessStatus;
import com.planit.planit.config.jwt.UserPrincipal;
import com.planit.planit.config.oauth.CustomOAuth2UserService;
import com.planit.planit.member.service.MemberService;
import com.planit.planit.web.dto.auth.login.OAuthLoginDTO;

import com.planit.planit.web.dto.member.term.TermAgreementDTO;
import jakarta.servlet.http.HttpServletRequest;
import com.planit.planit.web.dto.member.MemberResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "MEMBER", description = "회원 관련 API")
public class MemberController {

    private final MemberService memberService;


    @Operation(summary = "idToken 기반 로그인/회원가입", description = "모바일 앱에서 받은 idToken을 검증하여 로그인 또는 회원가입을 처리합니다.")
    @PostMapping("/sign-in")
    public ApiResponse<OAuthLoginDTO.Response> signIn(@RequestBody OAuthLoginDTO.Request request) {
        OAuthLoginDTO.Response response = memberService.signIn(request);
        log.info("✅ 로그인 or 회원가입 성공 - id: {}, email: {}, name: {}, isNewMember: {}, 약관 동의여부: {}",
                response.getId(),response.getEmail(), response.getName(), response.isNewMember(), response.isSignUpCompleted());
        return ApiResponse.onSuccess(MemberSuccessStatus.SIGN_IN_SUCCESS, response);
    }

    @Operation(summary = "로그아웃", description = "사용자 로그아웃을 처리하고 토큰을 블랙리스트에 추가합니다.")
    @SecurityRequirement(name = "accessToken")
    @PostMapping("/sign-out")
    public ApiResponse<Void> signOut(@AuthenticationPrincipal UserPrincipal principal, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring("Bearer ".length());
            memberService.signOut(principal.getId(), accessToken);
            log.info("✅ 로그아웃 성공 - id: {}, accessToken: {}...", principal.getId(), accessToken.substring(0, 10));
        }
        return ApiResponse.onSuccess(MemberSuccessStatus.SIGN_OUT_SUCCESS, null);
    }

    @Operation(summary = "약관 동의 완료", description = "사용자가 약관에 동의했음을 저장하고 isSignUpCompleted를 true로 갱신합니다.")
    @SecurityRequirement(name = "accessToken")
    @PostMapping("/terms")
    public ApiResponse<Void> agreeTerms(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody TermAgreementDTO.Request request
    ) {
        memberService.completeTermsAgreement(principal.getId(), request);
        return ApiResponse.onSuccess(MemberSuccessStatus.TERM_AGREEMENT_COMPLETED, null);
    }


    @Operation(summary = "[MEMBER] 연속일 조회하기")
    @GetMapping("/members/consecutive-days")
    public ApiResponse<MemberResponseDTO.ConsecutiveDaysDTO> getConsecutiveDays() {
        Long memberId = 1L; // 인증 기능 구현 이후 변경
        MemberResponseDTO.ConsecutiveDaysDTO consecutiveDaysDTO = memberService.getConsecutiveDays(memberId);
        return ApiResponse.onSuccess(MemberSuccessStatus.CONSECUTIVE_DAYS_FOUND, consecutiveDaysDTO);
    }


}
