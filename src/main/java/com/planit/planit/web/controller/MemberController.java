package com.planit.planit.web.controller;


import com.planit.planit.member.service.FcmTokenService;
import com.planit.planit.member.service.TermService;
import com.planit.planit.common.api.ApiResponse;
import com.planit.planit.common.api.member.status.MemberSuccessStatus;
import com.planit.planit.auth.jwt.UserPrincipal;
import com.planit.planit.member.service.MemberService;
import com.planit.planit.member.service.NotificationService;

import com.planit.planit.web.dto.member.MemberInfoResponseDTO;
import com.planit.planit.web.dto.member.fcmtoken.FcmTokenDTO;
import com.planit.planit.web.dto.member.notification.NotificationDTO;
import com.planit.planit.web.dto.member.term.TermAgreementDTO;
import com.planit.planit.web.dto.member.MemberResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/planit/members")
@Tag(name = "MEMBER", description = "회원 관련 API")
public class MemberController {

    private final MemberService memberService;
    private final TermService termService;
    private final NotificationService notificationService;
    private final FcmTokenService fcmTokenService;


    @Operation(summary = "[TERM] 약관 동의 완료", description = "사용자가 약관에 동의했음을 저장하고 isSignUpCompleted를 true로 갱신합니다.")
    @SecurityRequirement(name = "accessToken")
    @PostMapping("/terms")
    public ApiResponse<Void> agreeTerms(
            @RequestBody String oauthToken,
            @RequestBody TermAgreementDTO.Request request
    ) {
        memberService.completeTermsAgreement(oauthToken, request);
        return ApiResponse.onSuccess(MemberSuccessStatus.TERM_AGREEMENT_COMPLETED, null);
    }

    @Operation(summary = "[TERM] 모든 약관 URL 조회", description = "최신 약관 HTML 파일들의 URL과 버전을 반환합니다.")
    @GetMapping("/terms")
    public ApiResponse<Map<String, Map<String, String>>> getTermsUrls() {
        Map<String, Map<String, String>> termsInfo = termService.getAllTermsUrls();
        log.info("✅ 약관 URL 정보 조회 성공: {}", termsInfo);
        return ApiResponse.onSuccess(MemberSuccessStatus.TERMS_URLS_FOUND, termsInfo);
    }


    @Operation(summary = "[MEMBER] 연속일 조회하기")
    @GetMapping("/consecutive-days")
    public ApiResponse<MemberResponseDTO.ConsecutiveDaysDTO> getConsecutiveDays(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        MemberResponseDTO.ConsecutiveDaysDTO consecutiveDaysDTO = memberService.getConsecutiveDays(principal.getId());
        return ApiResponse.onSuccess(MemberSuccessStatus.CONSECUTIVE_DAYS_FOUND, consecutiveDaysDTO);
    }

    // 오늘의 할 일 알림 ON/OFF
    @Operation(summary = "[NOTIFICATION] 오늘의 할 일 알림 설정 변경", description = "오늘의 할 일 알림 ON/OFF를 설정합니다.")
    @PatchMapping("/notification-settings/daily-task")
    public ApiResponse<Void> updateDailyTask(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody NotificationDTO.ToggleRequest request
    ) {
        notificationService.updateDailyTaskNotification(principal.getId(), request);
        return ApiResponse.onSuccess(MemberSuccessStatus.NOTIFICATION_DAILY_TASK_TOGGLED);
    }

    // 길티프리 모드 알림 ON/OFF
    @Operation(summary = "[NOTIFICATION] 길티프리 모드 알림 설정 변경", description = "길티프리 모드 알림 ON/OFF를 설정합니다.")
    @PatchMapping("/notification-settings/guilty-free")
    public ApiResponse<Void> updateGuiltFree(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody NotificationDTO.ToggleRequest request
    ) {
        notificationService.updateGuiltFreeNotification(principal.getId(), request);
        return ApiResponse.onSuccess(MemberSuccessStatus.NOTIFICATION_GUILTY_FREE_TOGGLED);
    }

    // 전체 알림 설정 조회
    @Operation(summary = "[NOTIFICATION] 전체 알림 설정 조회", description = "사용자의 전체 알림 설정 상태를 조회합니다.")
    @GetMapping("/notification-settings")
    public ApiResponse<NotificationDTO.Response> getNotificationSetting(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        NotificationDTO.Response response = notificationService.getMyNotificationSetting(principal.getId());
        return ApiResponse.onSuccess(MemberSuccessStatus.NOTIFICATION_SETTING_FETCHED, response);
    }

    //사용자 정보 조회
    @Operation(summary = "[MEMBER] 내 정보 조회", description = "로그인한 사용자의 정보를 조회합니다.")
    @SecurityRequirement(name = "accessToken")
    @GetMapping("")
    public ApiResponse<MemberInfoResponseDTO> getMyInfo(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        MemberInfoResponseDTO response = memberService.getMemberInfo(principal.getId());
        return ApiResponse.onSuccess(MemberSuccessStatus.MEMBER_INFO_FETCHED, response);
    }

    @Operation(summary = "[FCM] FCM 토큰 저장 또는 갱신", description = "로그인한 사용자의 FCM 토큰을 저장하거나 갱신합니다.")
    @SecurityRequirement(name = "accessToken")
    @PostMapping("/me/fcm-token")
    public ApiResponse<Void> saveOrUpdateFcmToken(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody FcmTokenDTO.SaveRequest request
    ) {
        fcmTokenService.saveOrUpdateFcmToken(principal.getId(), request.getToken());
        log.info("✅ FCM 토큰 저장 완료 - memberId: {}, tokenPrefix: {}***",
        principal.getId(),
        request.getToken().length() > 10 ? request.getToken().substring(0, 10) : "short");
        return ApiResponse.onSuccess(MemberSuccessStatus.FCM_TOKEN_SAVED);
    }

    @Operation(summary = "[FCM] 내 FCM 토큰 조회", description = "로그인한 사용자의 저장된 FCM 토큰을 조회합니다.")
    @SecurityRequirement(name = "accessToken")
    @GetMapping("/me/fcm-token")
    public ApiResponse<FcmTokenDTO.Response> getMyFcmToken(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        String token = fcmTokenService.getTokenByMemberId(principal.getId()).orElse(null);
        return ApiResponse.onSuccess(MemberSuccessStatus.FCM_TOKEN_FOUND, new FcmTokenDTO.Response(token));
    }

    @Operation(summary = "[FCM] 내 FCM 토큰 삭제", description = "로그인한 사용자의 FCM 토큰을 삭제합니다.")
    @SecurityRequirement(name = "accessToken")
    @DeleteMapping("/me/fcm-token")
    public ApiResponse<Void> deleteMyFcmToken(@AuthenticationPrincipal UserPrincipal principal) {
        fcmTokenService.deleteTokensByMemberId(principal.getId());
        log.info("🗑️ FCM 토큰 삭제 완료 - memberId: {}", principal.getId());
        return ApiResponse.onSuccess(MemberSuccessStatus.FCM_TOKEN_DELETED);
    }
}
