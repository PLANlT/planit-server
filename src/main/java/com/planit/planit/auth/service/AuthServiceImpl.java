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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        // 토큰 생성
        final String accessToken = jwtProvider.createAccessToken(
                signedMember.getId(), signedMember.getEmail(), signedMember.getName(), signedMember.getRole());
        String refreshToken = refreshTokenRedisService.getRefreshTokenByMemberId(signedMember.getId());
        if (refreshToken == null || jwtProvider.isTokenExpired(refreshToken)) {
            refreshToken = jwtProvider.createRefreshToken(signedMember.getId(), signedMember.getEmail(), signedMember.getName(), signedMember.getRole());
            refreshTokenRedisService.saveRefreshToken(signedMember.getId(), refreshToken);
        }

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
        if(jwtProvider.isTokenExpired(refreshToken)) {
            throw new TokenHandler(TokenErrorStatus.REFRESH_TOKEN_EXPIRED);
        }

        if (jwtProvider.isRefreshTokenTampered(refreshToken)) {
            throw new TokenHandler(TokenErrorStatus.INVALID_REFRESH_TOKEN); // 진짜 위조된 경우만
        }

        Long memberId = jwtProvider.getId(refreshToken);

        String savedToken = refreshTokenRedisService.getRefreshTokenByMemberId(memberId);
        if (!refreshToken.equals(savedToken)) {
            throw new TokenHandler(TokenErrorStatus.INVALID_REFRESH_TOKEN);
        }

        // 로그인한 회원 정보 조회
        SignedMember signedMember = memberService.getSignedMemberById(memberId);

        String newAccessToken = jwtProvider.createAccessToken(
                signedMember.getId(), signedMember.getEmail(), signedMember.getName(), signedMember.getRole()
        );

        return TokenRefreshDTO.Response.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // Refresh는 만료되면 로그인 다시해야함
                .build();
    }


}
