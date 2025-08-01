package com.planit.planit.auth.service;

import com.planit.planit.auth.jwt.JwtProvider;
import com.planit.planit.auth.oauth.SocialTokenVerifier;
import com.planit.planit.common.api.general.GeneralException;
import com.planit.planit.common.api.token.TokenHandler;
import com.planit.planit.common.api.token.status.TokenErrorStatus;
import com.planit.planit.member.association.SignedMember;
import com.planit.planit.member.enums.SignType;
import com.planit.planit.member.service.MemberService;
import com.planit.planit.web.dto.auth.OAuthLoginDTO;
import com.planit.planit.web.dto.auth.TokenRefreshDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberService memberService;

    private final BlacklistTokenRedisService blacklistTokenRedisService;
    private final RefreshTokenRedisService refreshTokenRedisService;
    private final SocialTokenVerifier  socialTokenVerifier;
    private final JwtProvider jwtProvider;


    @Override
    public OAuthLoginDTO.LoginResponse signIn(OAuthLoginDTO.LoginRequest loginRequest) {
        SocialTokenVerifier.SocialUserInfo userInfo;
        try {
            userInfo = socialTokenVerifier.verify(loginRequest.getOauthProvider(), loginRequest.getOauthToken());
        } catch (Exception e) {
            throw new GeneralException(TokenErrorStatus.INVALID_ID_TOKEN);
        }

        // 로그인한 회원 정보 조회
        final SignedMember signedMember = memberService.getSignedMemberByUserInfo(
                userInfo.getEmail(), userInfo.getName(), SignType.valueOf(loginRequest.getOauthProvider().toUpperCase()));

        // 회원가입이 완료되지 않은 회원 > 가입용 토큰 생성
        if (signedMember.getIsSignUpCompleted() == false) {
            final String signUpToken = jwtProvider.createSignUpToken(signedMember);
            return OAuthLoginDTO.LoginResponse.of(signedMember, signUpToken, null);
        }

        // 회원가입이 완료된 회원 > 액세스 토큰과 리프레시 토큰 생성
        final String accessToken = jwtProvider.createAccessToken(
                signedMember.getId(), signedMember.getEmail(), signedMember.getName(), signedMember.getRole());
        String refreshToken = refreshTokenRedisService.getRefreshTokenByMemberId(signedMember.getId());
        if (refreshToken == null || jwtProvider.isTokenExpired(refreshToken)) {
            refreshToken = jwtProvider.createRefreshToken(signedMember.getId(), signedMember.getEmail(), signedMember.getName(), signedMember.getRole());
            refreshTokenRedisService.saveRefreshToken(signedMember.getId(), refreshToken);
        }

        // 리프레시 토큰 유효기간 로그 출력
        long refreshTokenRemainingValidity = jwtProvider.getRemainingValidity(refreshToken);
        long refreshTokenRemainingDays = refreshTokenRemainingValidity / (24 * 60 * 60); // 일 단위로 변환
        long refreshTokenRemainingHours = (refreshTokenRemainingValidity % (24 * 60 * 60)) / (60 * 60); // 시간 단위로 변환
        
        log.info("🔐 로그인 성공 - Member ID: {}, Email: {}, Refresh Token 유효기간: {}일 {}시간 남음 (총 {}초)", 
                signedMember.getId(), signedMember.getEmail(), 
                refreshTokenRemainingDays, refreshTokenRemainingHours, refreshTokenRemainingValidity);

        return OAuthLoginDTO.LoginResponse.of(signedMember, accessToken, refreshToken);
    }

    @Override
    public void signOut(Long memberId, String accessToken) {
        // accessToken 남은 만료시간 계산
        long ttl = jwtProvider.getRemainingValidity(accessToken);
        blacklistTokenRedisService.blacklistAccessToken(accessToken, ttl);
        refreshTokenRedisService.deleteByMemberId(memberId);
    }

    @Override
    public TokenRefreshDTO.Response refreshAccessToken(String refreshToken) {
        log.info("🔄 토큰 갱신 시작 - Refresh Token: {}...", refreshToken.substring(0, Math.min(20, refreshToken.length())));
        
        // 1. Refresh Token 만료 여부 확인
        if(jwtProvider.isTokenExpired(refreshToken)) {
            log.warn("❌ 토큰 갱신 실패 - Refresh Token 만료됨");
            throw new TokenHandler(TokenErrorStatus.REFRESH_TOKEN_EXPIRED);
        }

        // 2. Refresh Token 위조 여부 확인
        if (jwtProvider.isRefreshTokenTampered(refreshToken)) {
            log.warn("❌ 토큰 갱신 실패 - Refresh Token 위조됨");
            throw new TokenHandler(TokenErrorStatus.INVALID_REFRESH_TOKEN); // 진짜 위조된 경우만
        }

        Long memberId = jwtProvider.getId(refreshToken);
        log.info("🔄 토큰 갱신 진행 - Member ID: {}", memberId);

        // 3. Redis에 저장된 토큰과 일치 여부 확인
        String savedToken = refreshTokenRedisService.getRefreshTokenByMemberId(memberId);
        if (!refreshToken.equals(savedToken)) {
            log.warn("❌ 토큰 갱신 실패 - Redis에 저장된 토큰과 불일치, Member ID: {}", memberId);
            throw new TokenHandler(TokenErrorStatus.INVALID_REFRESH_TOKEN);
        }

        // 4. 회원 정보 조회
        SignedMember signedMember = memberService.getSignedMemberById(memberId);
        log.info("🔄 회원 정보 조회 완료 - Member ID: {}, Email: {}, Name: {}", 
                signedMember.getId(), signedMember.getEmail(), signedMember.getName());

        // 5. 새로운 Access Token 생성
        String newAccessToken = jwtProvider.createAccessToken(
                signedMember.getId(), signedMember.getEmail(), signedMember.getName(), signedMember.getRole()
        );

        // 6. 새로운 Access Token의 유효기간 로그
        long newAccessTokenRemainingValidity = jwtProvider.getRemainingValidity(newAccessToken);
        long newAccessTokenRemainingHours = newAccessTokenRemainingValidity / (60 * 60); // 시간 단위로 변환
        long newAccessTokenRemainingMinutes = (newAccessTokenRemainingValidity % (60 * 60)) / 60; // 분 단위로 변환
        
        log.info("✅ 토큰 갱신 성공 - Member ID: {}, 새로운 Access Token 유효기간: {}시간 {}분 남음 (총 {}초)", 
                signedMember.getId(), newAccessTokenRemainingHours, newAccessTokenRemainingMinutes, newAccessTokenRemainingValidity);

        return TokenRefreshDTO.Response.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // Refresh는 만료되면 로그인 다시해야함
                .id(signedMember.getId())
                .email(signedMember.getEmail())
                .name(signedMember.getName())
                .role(signedMember.getRole().toString())
                .build();
    }


}
