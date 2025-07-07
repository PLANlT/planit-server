package com.planit.planit.member.service;

import com.planit.planit.common.api.general.GeneralException;
import com.planit.planit.common.api.general.status.ErrorStatus;
import com.planit.planit.common.api.member.MemberHandler;
import com.planit.planit.common.api.member.status.MemberErrorStatus;
import com.planit.planit.config.jwt.JwtProvider;
import com.planit.planit.config.oauth.CustomOAuth2User;
import com.planit.planit.config.oauth.SocialTokenVerifier;
import com.planit.planit.member.Member;
import com.planit.planit.member.repository.MemberRepository;
import com.planit.planit.member.association.Term;
import com.planit.planit.member.repository.TermRepository;
import com.planit.planit.member.enums.Role;
import com.planit.planit.member.enums.SignType;
import com.planit.planit.web.dto.auth.login.OAuthLoginDTO;
import com.planit.planit.web.dto.member.term.TermAgreementDTO;
import com.planit.planit.redis.service.RefreshTokenRedisService;
import com.planit.planit.redis.service.BlacklistTokenRedisService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final TermRepository termRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRedisService refreshTokenRedisService;
    private final BlacklistTokenRedisService blacklistTokenRedisService;
    private final SocialTokenVerifier  socialTokenVerifier;

    @Override
    public OAuthLoginDTO.Response signIn(OAuthLoginDTO.Request request) {
        SocialTokenVerifier.SocialUserInfo userInfo;
        try {
            userInfo = socialTokenVerifier.verify(request.getOauthProvider(), request.getOauthToken());
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.INVALID_ID_TOKEN);
        }
        Optional<Member> memberOpt = memberRepository.findByEmail(userInfo.email);
        final boolean isNewMember;
        final Member member;
        if (memberOpt.isPresent()) {
            member = memberOpt.get();
            isNewMember = false;
        } else {
            member = Member.builder()
                .email(userInfo.email)
                .memberName(userInfo.name)
                .password(UUID.randomUUID().toString().substring(0, 10))
                .signType(SignType.valueOf(request.getOauthProvider().toUpperCase()))
                .guiltyFreeMode(false)
                .dailyCondition(null)
                .role(Role.USER)
                .build();
            memberRepository.save(member);
            isNewMember = true;
        }
        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail(), member.getMemberName(), member.getRole());
        String refreshToken = jwtProvider.createRefreshToken(member.getId(), member.getEmail(), member.getMemberName(), member.getRole());
        return OAuthLoginDTO.Response.builder()
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
    public OAuthLoginDTO.Response signInWithIdToken(OAuthLoginDTO.Request request) {
        SocialTokenVerifier.SocialUserInfo userInfo;
        try {
            userInfo = socialTokenVerifier.verify(request.getOauthProvider(), request.getOauthToken()); // ✅ 인스턴스 메서드 사용
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.INVALID_ID_TOKEN);
        }

        // 1. 회원 조회 또는 생성 (isNewMember 플래그 분리)
        Optional<Member> memberOpt = memberRepository.findByEmail(userInfo.email);
        final boolean isNewMember;
        final Member member;
        if (memberOpt.isPresent()) {
            member = memberOpt.get();
            isNewMember = false;
        } else {
            member = Member.builder()
                .email(userInfo.email)
                .memberName(userInfo.name)
                .password(UUID.randomUUID().toString().substring(0, 10))
                .signType(SignType.valueOf(request.getOauthProvider().toUpperCase()))
                .guiltyFreeMode(false) // 기본값 예시
                .dailyCondition(null)  // 필요시 설정
                .role(Role.USER)       // 기본 USER 권한
                .build();
            memberRepository.save(member);
            isNewMember = true;
        }

        // 2. JWT 발급
        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail(), member.getMemberName(), member.getRole());
        String refreshToken = jwtProvider.createRefreshToken(member.getId(), member.getEmail(), member.getMemberName(), member.getRole());

        // 3. 응답 반환
        return OAuthLoginDTO.Response.builder()
            .email(member.getEmail())
            .name(member.getMemberName())
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .isNewMember(isNewMember)
            .isSignUpCompleted(true) // 약관 등 추가 로직 필요시 수정
            .build();
    }
}
