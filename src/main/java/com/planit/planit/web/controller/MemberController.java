package com.planit.planit.web.controller;


import com.planit.planit.agreement.service.AgreementService;
import com.planit.planit.common.api.ApiResponse;
import com.planit.planit.common.api.member.status.MemberSuccessStatus;
import com.planit.planit.config.jwt.UserPrincipal;
import com.planit.planit.config.oauth.CustomOAuth2UserService;
import com.planit.planit.member.service.FcmTokenService;
import com.planit.planit.member.service.MemberService;
import com.planit.planit.member.service.NotificationService;
import com.planit.planit.web.dto.auth.login.OAuthLoginDTO;

import com.planit.planit.web.dto.member.MemberInfoResponseDTO;
import com.planit.planit.web.dto.member.fcmtoken.FcmTokenDTO;
import com.planit.planit.web.dto.member.notification.NotificationDTO;
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

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "MEMBER", description = "회원 관련 API")
public class MemberController {

    private final MemberService memberService;
    private final AgreementService agreementService;
    private final NotificationService notificationService;
    private final FcmTokenService fcmTokenService;


    @Operation(summary = "[MEMBER] idToken 기반 로그인/회원가입", description = "모바일 앱에서 받은 idToken을 검증하여 로그인 또는 회원가입을 처리합니다.")
    @PostMapping("/sign-in")
    public ApiResponse<OAuthLoginDTO.Response> signIn(@RequestBody OAuthLoginDTO.Request request) {
        OAuthLoginDTO.Response response = memberService.signIn(request);
        log.info("✅ 로그인 or 회원가입 성공 - id: {}, email: {}, name: {}, isNewMember: {}, 약관 동의여부: {}",
                response.getId(),response.getEmail(), response.getName(), response.isNewMember(), response.isSignUpCompleted());
        return ApiResponse.onSuccess(MemberSuccessStatus.SIGN_IN_SUCCESS, response);
    }

    @Operation(summary = "[MEMBER] 로그아웃", description = "사용자 로그아웃을 처리하고 토큰을 블랙리스트에 추가합니다.")
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

    @Operation(summary = "[POLICY] 모든 약관 URL 조회", description = "최신 약관 HTML 파일들의 URL과 버전을 반환합니다.")
    @GetMapping("/terms")
    public ApiResponse<Map<String, Map<String, String>>> getTermsUrls() {
        Map<String, Map<String, String>> termsInfo = agreementService.getAllTermsUrls();
        log.info("✅ 약관 URL 정보 조회 성공: {}", termsInfo);
        return ApiResponse.onSuccess(MemberSuccessStatus.TERMS_URLS_FOUND, termsInfo);
    }


    @Operation(summary = "[MEMBER] 연속일 조회하기")
    @GetMapping("/members/consecutive-days")
    public ApiResponse<MemberResponseDTO.ConsecutiveDaysDTO> getConsecutiveDays() {
        Long memberId = 1L; // 인증 기능 구현 이후 변경
        MemberResponseDTO.ConsecutiveDaysDTO consecutiveDaysDTO = memberService.getConsecutiveDays(memberId);
        return ApiResponse.onSuccess(MemberSuccessStatus.CONSECUTIVE_DAYS_FOUND, consecutiveDaysDTO);
    }

    // 오늘의 할 일 알림 ON/OFF
    @Operation(summary = "[NOTIFICATION] 오늘의 할 일 알림 설정 변경", description = "오늘의 할 일 알림 ON/OFF를 설정합니다.")
    @PatchMapping("/me/notification-settings/daily-task")
    public ApiResponse<Void> updateDailyTask(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody NotificationDTO.ToggleRequest request
    ) {
        notificationService.updateDailyTaskNotification(principal.getId(), request);
        return ApiResponse.onSuccess(MemberSuccessStatus.NOTIFICATION_DAILY_TASK_TOGGLED);
    }

    // 길티프리 모드 알림 ON/OFF
    @Operation(summary = "[NOTIFICATION] 길티프리 모드 알림 설정 변경", description = "길티프리 모드 알림 ON/OFF를 설정합니다.")
    @PatchMapping("/me/notification-settings/guilty-free")
    public ApiResponse<Void> updateGuiltFree(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody NotificationDTO.ToggleRequest request
    ) {
        notificationService.updateGuiltFreeNotification(principal.getId(), request);
        return ApiResponse.onSuccess(MemberSuccessStatus.NOTIFICATION_GUILTY_FREE_TOGGLED);
    }

    // 전체 알림 설정 조회
    @Operation(summary = "[NOTIFICATION] 전체 알림 설정 조회", description = "사용자의 전체 알림 설정 상태를 조회합니다.")
    @GetMapping("/me/notification-settings")
    public ApiResponse<NotificationDTO.Response> getNotificationSetting(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        NotificationDTO.Response response = notificationService.getMyNotificationSetting(principal.getId());
        return ApiResponse.onSuccess(MemberSuccessStatus.NOTIFICATION_SETTING_FETCHED, response);
    }

    //사용자 정보 조회
    @Operation(summary = "[MEMBER] 내 정보 조회", description = "로그인한 사용자의 정보를 조회합니다.")
    @SecurityRequirement(name = "accessToken")
    @GetMapping("/me")
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
        log.info("✅ FCM 토큰 저장 완료: {}", request.getToken());
        return ApiResponse.onSuccess(MemberSuccessStatus.FCM_TOKEN_SAVED);
    }

    @Operation(summary = "[FCM] 내 FCM 토큰 조회", description = "로그인한 사용자의 저장된 FCM 토큰을 조회합니다.")
    @SecurityRequirement(name = "accessToken")
    @GetMapping("/me/fcm-token")
    public ApiResponse<FcmTokenDTO.Response> getMyFcmToken(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        String token = fcmTokenService.getTokenByMemberId(principal.getId()).orElse(null);
        log.info("✅ FCM 토큰 조회 완료 - memberId: {}, token: {}", principal.getId(), token);
        return ApiResponse.onSuccess(MemberSuccessStatus.FCM_TOKEN_FOUND, new FcmTokenDTO.Response(token));
    }

    @Operation(summary = "[FCM] FCM 토큰 삭제", description = "로그인한 사용자의 FCM 토큰을 삭제합니다.")
    @SecurityRequirement(name = "accessToken")
    @DeleteMapping("/me/fcm-token")
    public ApiResponse<Void> deleteMyFcmToken(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody FcmTokenDTO.DeleteRequest request
    ) {
        fcmTokenService.deleteToken(request.getToken());
        log.info("🗑️ FCM 토큰 삭제 완료 - memberId: {}, token: {}", principal.getId(), request.getToken());
        return ApiResponse.onSuccess(MemberSuccessStatus.FCM_TOKEN_DELETED);
    }
}
