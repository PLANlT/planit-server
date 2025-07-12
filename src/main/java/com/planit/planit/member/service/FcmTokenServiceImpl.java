package com.planit.planit.member.service;

import com.planit.planit.common.api.member.MemberHandler;
import com.planit.planit.common.api.member.status.MemberErrorStatus;
import com.planit.planit.member.Member;
import com.planit.planit.member.association.FcmToken;
import com.planit.planit.member.repository.FcmTokenRepository;
import com.planit.planit.member.repository.MemberRepository;
import com.planit.planit.member.service.FcmTokenService;

import java.util.Optional;

public class FcmTokenServiceImpl implements FcmTokenService {

    private FcmTokenRepository fcmTokenRepository;

    private MemberRepository memberRepository;

    @Override
    public void saveOrUpdateFcmToken(Long memberId, String token) {
        Optional<FcmToken> optional = fcmTokenRepository.findById(memberId);

        if (optional.isPresent()) {
            FcmToken existing = optional.get();
            existing.setToken(token);
            existing.updateLastUsedAt();
            fcmTokenRepository.save(existing);
        } else {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));

            FcmToken newToken = FcmToken.of(member, token);
            fcmTokenRepository.save(newToken);
        }
    }
}