package com.planit.planit.web.controller;


import com.planit.planit.common.api.ApiResponse;
import com.planit.planit.common.api.member.status.MemberSuccessStatus;
import com.planit.planit.config.jwt.UserPrincipal;
import com.planit.planit.member.service.MemberService;
import com.planit.planit.web.dto.auth.login.OAuthLoginDTO;

import com.planit.planit.web.dto.member.MemberResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/planit")
@Tag(name = "MEMBER", description = "회원 관련 API")
public class MemberController {

    private final MemberService memberService;


    public ResponseEntity<OAuthLoginDTO.Response> signIn(OAuthLoginDTO.Request request) {

        return null;
    }

    public ResponseEntity<Void> signOut(UserPrincipal principal) {
        return null;
    }

    @Operation(summary = "[MEMBER] 연속일 조회하기")
    @GetMapping("/members/consecutive-days")
    public ApiResponse<MemberResponseDTO.ConsecutiveDaysDTO> getConsecutiveDays() {
        Long memberId = 1L; // 인증 기능 구현 이후 변경
        MemberResponseDTO.ConsecutiveDaysDTO consecutiveDaysDTO = memberService.getConsecutiveDays(memberId);
        return ApiResponse.onSuccess(MemberSuccessStatus.CONSECUTIVE_DAYS_FOUND, consecutiveDaysDTO);
    }
}
