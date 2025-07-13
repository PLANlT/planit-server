package com.planit.planit.auth.service;

import com.planit.planit.auth.jwt.JwtProvider;
import com.planit.planit.auth.oauth.SocialTokenVerifier;
import com.planit.planit.common.api.general.GeneralException;
import com.planit.planit.common.api.member.status.MemberErrorStatus;
import com.planit.planit.common.api.token.TokenHandler;
import com.planit.planit.common.api.token.status.TokenErrorStatus;
import com.planit.planit.member.Member;
import com.planit.planit.member.association.GuiltyFree;
import com.planit.planit.member.association.Notification;
import com.planit.planit.member.enums.GuiltyFreeReason;
import com.planit.planit.member.enums.Role;
import com.planit.planit.member.enums.SignType;
import com.planit.planit.member.repository.GuiltyFreeRepository;
import com.planit.planit.member.repository.MemberRepository;
import com.planit.planit.member.repository.NotificationRepository;
import com.planit.planit.web.dto.auth.OAuthLoginDTO;
import com.planit.planit.web.dto.auth.TokenRefreshDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final LocalDate guiltyFreeInitDate = LocalDate.of(2000, 1, 1);

    private final MemberRepository memberRepository;
    private final GuiltyFreeRepository guiltyFreeRepository;
    private final BlacklistTokenRedisService blacklistTokenRedisService;
    private final SocialTokenVerifier  socialTokenVerifier;
    private final NotificationRepository notificationRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRedisService refreshTokenRedisService;

    @Override
    public OAuthLoginDTO.LoginResponse signIn(OAuthLoginDTO.LoginRequest loginRequest) {
        SocialTokenVerifier.SocialUserInfo userInfo;
        try {
            userInfo = socialTokenVerifier.verify(loginRequest.getOauthProvider(), loginRequest.getOauthToken());
        } catch (Exception e) {
            throw new GeneralException(TokenErrorStatus.INVALID_ID_TOKEN);
        }
        Optional<Member> memberOpt = memberRepository.findByEmail(userInfo.email);
        final boolean isNewMember;
        final Member member;
        if (memberOpt.isPresent()) {
            member = memberOpt.get();
            if (!member.getSignType().name().equalsIgnoreCase(loginRequest.getOauthProvider())) {
                throw new GeneralException(MemberErrorStatus.DIFFERENT_SIGN_TYPE);
            }
            isNewMember = false;
        } else {

            // 회원 정보 저장
            member = Member.builder()
                    .email(userInfo.email)
                    .memberName(userInfo.name)
                    .password(UUID.randomUUID().toString().substring(0, 10))
                    .signType(SignType.valueOf(loginRequest.getOauthProvider().toUpperCase()))
                    .guiltyFreeMode(false)
                    .dailyCondition(null)
                    .role(Role.USER)
                    .build();
            memberRepository.save(member);

            // 길티프리 설정 초기화
            GuiltyFree guiltyFree = GuiltyFree.of(member, GuiltyFreeReason.NONE, guiltyFreeInitDate);
            guiltyFreeRepository.save(guiltyFree);

            // 알림 설정 저장
            Notification notification = Notification.of(member);
            notificationRepository.save(notification);
            isNewMember = true;
        }
        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail(), member.getMemberName(), member.getRole());
        String refreshToken = refreshTokenRedisService.getRefreshTokenByMemberId(member.getId());
        if (refreshToken == null || jwtProvider.isTokenExpired(refreshToken)) {
            refreshToken = jwtProvider.createRefreshToken(member.getId(), member.getEmail(), member.getMemberName(), member.getRole());
            refreshTokenRedisService.saveRefreshToken(member.getId(), refreshToken);
        }
        return OAuthLoginDTO.LoginResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getMemberName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNewMember(isNewMember)
                .isSignUpCompleted(false)
                .build();
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

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(MemberErrorStatus.MEMBER_NOT_FOUND));

        String newAccessToken = jwtProvider.createAccessToken(
                member.getId(), member.getEmail(), member.getMemberName(), member.getRole()
        );

        return TokenRefreshDTO.Response.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // Refresh는 만료되면 로그인 다시해야함
                .build();
    }


}
