package com.planit.planit.member.service;

import com.planit.planit.common.api.member.MemberHandler;
import com.planit.planit.common.api.member.status.MemberErrorStatus;
import com.planit.planit.config.jwt.JwtProvider;
import com.planit.planit.config.oauth.CustomOAuth2User;
import com.planit.planit.member.Member;
import com.planit.planit.member.repository.MemberRepository;
import com.planit.planit.member.association.Term;
import com.planit.planit.member.repository.TermRepository;
import com.planit.planit.member.enums.Role;
import com.planit.planit.member.enums.SignType;
import com.planit.planit.web.dto.auth.login.OAuthLoginDTO;
import com.planit.planit.web.dto.member.MemberResponseDTO;
import com.planit.planit.web.dto.member.term.TermAgreementDTO;
import com.planit.planit.redis.service.RefreshTokenRedisService;
import com.planit.planit.redis.service.BlacklistTokenRedisService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

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

    //로그인인지 회원가입인지 감지
    @Override
    public OAuthLoginDTO.Response checkOAuthMember(CustomOAuth2User oAuth2User) {
        String email = (String) oAuth2User.getAttributes().get("email");
        String name = (String) oAuth2User.getAttributes().get("name");

        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();

            String accessToken = jwtProvider.createAccessToken(
                    member.getId(), member.getEmail(), member.getMemberName(), member.getRole()
            );

            String refreshToken = jwtProvider.createRefreshToken(
                    member.getId(), member.getEmail(), member.getMemberName(), member.getRole()
            );

            return OAuthLoginDTO.Response.builder()
                    .email(member.getEmail())
                    .name(member.getMemberName())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .isNewMember(false)
                    .build();
        }

        // 신규 회원 → 약관 동의 필요
        return OAuthLoginDTO.Response.builder()
                .email(email)
                .name(name)
                .accessToken(null)
                .refreshToken(null)
                .isNewMember(true)
                .build();
    }



    @Override
    public OAuthLoginDTO.Response registerOAuthMember(CustomOAuth2User oAuth2User, TermAgreementDTO.Request request) {
        String email = (String) oAuth2User.getAttributes().get("email");
        String name = (String) oAuth2User.getAttributes().get("name");
        SignType signType = oAuth2User.getSignType();


        //혹시 오류 생길까봐 넣긴 했는데, 중복 확인이라 고민입니다
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new MemberHandler(MemberErrorStatus.MEMBER_ALREADY_EXISTS);
        }

        Member member = Member.builder()
                .email(email)
                .memberName(name)
                .signType(SignType.GOOGLE)  // 또는 매개변수로 받아도 됨
                .guiltyFreeMode(false)
                .role(Role.USER)
                .build();
        memberRepository.save(member);

        Term term = Term.builder()
                .member(member)
                .termOfUse(request.getTermOfUse())
                .termOfPrivacy(request.getTermOfPrivacy())
                .build();
        termRepository.save(term);

        String accessToken = jwtProvider.createAccessToken(
                member.getId(), member.getEmail(), member.getMemberName(), member.getRole()
        );

        String refreshToken = jwtProvider.createRefreshToken(
                member.getId(), member.getEmail(), member.getMemberName(), member.getRole()
        );

        return OAuthLoginDTO.Response.builder()
                .email(member.getEmail())
                .name(member.getMemberName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNewMember(true)
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
    @Transactional(readOnly = true)
    public MemberResponseDTO.ConsecutiveDaysDTO getConsecutiveDays(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));
        return MemberResponseDTO.ConsecutiveDaysDTO.of(member);
    }
}
