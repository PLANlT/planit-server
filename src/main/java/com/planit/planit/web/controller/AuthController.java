package com.planit.planit.web.controller;

import com.planit.planit.auth.jwt.UserPrincipal;
import com.planit.planit.auth.service.AuthService;
import com.planit.planit.common.api.ApiResponse;
import com.planit.planit.common.api.general.GeneralException;
import com.planit.planit.common.api.member.status.MemberSuccessStatus;
import com.planit.planit.auth.docs.AuthDocs;
import com.planit.planit.common.api.token.status.TokenErrorStatus;
import com.planit.planit.web.dto.auth.OAuthLoginDTO;
import com.planit.planit.web.dto.auth.TokenRefreshDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.planit.planit.common.api.token.status.TokenSuccessStatus.REFRESH_SUCCESS;
import com.planit.planit.common.api.ApiErrorCodeExample;

@Slf4j
@RestController
@RequestMapping("/planit/auth")
@RequiredArgsConstructor
@Tag(name = "AUTH", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "[AUTH] idToken 기반 로그인/회원가입", description = "모바일 앱에서 받은 idToken을 검증하여 로그인 또는 회원가입을 처리합니다.")
    @ApiErrorCodeExample(value = com.planit.planit.common.api.member.status.MemberErrorStatus.class, codes = {"MEMBER_NOT_FOUND"})
    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<OAuthLoginDTO.LoginResponse>> signIn(
            @RequestBody OAuthLoginDTO.LoginRequest loginRequest
    ) {
        OAuthLoginDTO.LoginResponse loginResponse = authService.signIn(loginRequest);
        log.info("✅ 로그인 or 회원가입 성공 - id: {}, email: {}, name: {}, isNewMember: {}, 약관 동의여부: {}",
                 loginResponse.getId(), loginResponse.getEmail(), loginResponse.getName(),
                 loginResponse.isNewMember(), loginResponse.isSignUpCompleted());
        return ApiResponse.onSuccess(MemberSuccessStatus.SIGN_IN_SUCCESS, loginResponse);
    }

    @Operation(summary = "[AUTH] 로그아웃", description = "사용자 로그아웃을 처리하고 토큰을 블랙리스트에 추가합니다.")
    @SecurityRequirement(name = "accessToken")
    @ApiErrorCodeExample(value = com.planit.planit.common.api.member.status.MemberErrorStatus.class, codes = {"MEMBER_NOT_FOUND"})
    @PostMapping("/sign-out")
    public ResponseEntity<ApiResponse<Void>> signOut(
            @AuthenticationPrincipal UserPrincipal principal, HttpServletRequest request
    ) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring("Bearer ".length());
            authService.signOut(principal.getId(), accessToken);
            log.info("✅ 로그아웃 성공 - id: {}, accessToken: {}...", principal.getId(), accessToken.substring(0, 10));
        }
        return ApiResponse.onSuccess(MemberSuccessStatus.SIGN_OUT_SUCCESS, null);
    }

    @Operation(summary = "[AUTH] 토큰 리프레시", description = "리프레시 토큰을 Authorization 헤더로 전달받아 액세스 토큰을 재발급합니다.")
    @ApiErrorCodeExample(value = com.planit.planit.common.api.token.status.TokenErrorStatus.class, codes = {"INVALID_REFRESH_TOKEN"})
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenRefreshDTO.Response>> refreshToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String refreshTokenHeader
    ) {
        if (refreshTokenHeader == null || !refreshTokenHeader.startsWith("Bearer ")) {
            throw new GeneralException(TokenErrorStatus.INVALID_REFRESH_TOKEN);
        }

        String refreshToken = refreshTokenHeader.substring(7);
        TokenRefreshDTO.Response response = authService.refreshAccessToken(refreshToken);
        return ApiResponse.onSuccess(REFRESH_SUCCESS, response);
    }
}