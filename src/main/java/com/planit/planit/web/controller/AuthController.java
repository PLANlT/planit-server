package com.planit.planit.web.controller;

import com.planit.planit.auth.jwt.UserPrincipal;
import com.planit.planit.auth.service.AuthService;
import com.planit.planit.common.api.ApiResponse;
import com.planit.planit.common.api.member.status.MemberSuccessStatus;
import com.planit.planit.web.dto.auth.OAuthLoginDTO;
import com.planit.planit.web.dto.auth.TokenRefreshDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.planit.planit.common.api.token.status.TokenSuccessStatus.REFRESH_SUCCESS;

@Slf4j
@RestController
@RequestMapping("/planit/auth")
@RequiredArgsConstructor
@Tag(name = "AUTH", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "[AUTH] idToken 기반 로그인/회원가입", description = "모바일 앱에서 받은 idToken을 검증하여 로그인 또는 회원가입을 처리합니다.")
    @PostMapping("/sign-in")
    public ApiResponse<OAuthLoginDTO.LoginResponse> signIn(@RequestBody OAuthLoginDTO.LoginRequest loginRequest) {
        OAuthLoginDTO.LoginResponse loginResponse = authService.signIn(loginRequest);
        log.info("✅ 로그인 or 회원가입 성공 - id: {}, email: {}, name: {}, isNewMember: {}, 약관 동의여부: {}",
                loginResponse.getId(), loginResponse.getEmail(), loginResponse.getName(), loginResponse.isNewMember(), loginResponse.isSignUpCompleted());
        return ApiResponse.onSuccess(MemberSuccessStatus.SIGN_IN_SUCCESS, loginResponse);
    }

    @Operation(summary = "[AUTH] 로그아웃", description = "사용자 로그아웃을 처리하고 토큰을 블랙리스트에 추가합니다.")
    @SecurityRequirement(name = "accessToken")
    @PostMapping("/sign-out")
    public ApiResponse<Void> signOut(@AuthenticationPrincipal UserPrincipal principal, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring("Bearer ".length());
            authService.signOut(principal.getId(), accessToken);
            log.info("✅ 로그아웃 성공 - id: {}, accessToken: {}...", principal.getId(), accessToken.substring(0, 10));
        }
        return ApiResponse.onSuccess(MemberSuccessStatus.SIGN_OUT_SUCCESS, null);
    }

    @Operation(summary = "[AUTH] 토큰 리프레시", description = "사용자 리프레시 토큰을 초기화합니다.")
    @PostMapping("/refresh")
    public ApiResponse<TokenRefreshDTO.Response> refreshToken(@RequestBody TokenRefreshDTO.Request request) {
        TokenRefreshDTO.Response response = authService.refreshAccessToken(request.getRefreshToken());
        return ApiResponse.onSuccess(REFRESH_SUCCESS, response);
    }
}