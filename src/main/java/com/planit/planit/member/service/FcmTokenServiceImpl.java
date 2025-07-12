package com.planit.planit.member.service;

import com.planit.planit.common.api.member.MemberHandler;
import com.planit.planit.common.api.member.status.MemberErrorStatus;
import com.planit.planit.member.Member;
import com.planit.planit.member.association.FcmToken;
import com.planit.planit.member.repository.FcmTokenRepository;
import com.planit.planit.member.repository.MemberRepository;
import com.planit.planit.member.service.FcmTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmTokenServiceImpl implements FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void saveOrUpdateFcmToken(Long memberId, String token) { //저장 및 수정
        Optional<FcmToken> optional = fcmTokenRepository.findById(memberId);

        if (optional.isPresent()) {
            FcmToken existing = optional.get();
            existing.setToken(token);
            existing.updateLastUsedAt();
        } else {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));

            FcmToken newToken = FcmToken.of(member, token);
            fcmTokenRepository.save(newToken);
        }
    }

    @Override
    @Transactional
    public void deleteToken(String token) {         //아예 삭제
        fcmTokenRepository.deleteByToken(token);
    }

    @Override
    @Transactional
    public void deleteTokensByMemberId(Long memberId) { //memberId로 찾아서 전체 삭제할떄
        fcmTokenRepository.deleteById(memberId);
    }

    @Override
    @Transactional
    public void cleanUpInvalidTokens(List<String> invalidTokens) {
        fcmTokenRepository.deleteByTokenIn(invalidTokens);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> getTokenByMemberId(Long memberId) {
        Optional<String> tokenOpt = fcmTokenRepository.findById(memberId)
                .map(FcmToken::getToken);

        if (tokenOpt.isEmpty()) {
            log.info("❌ FCM 토큰 없음 - memberId: {}", memberId);
        } else {
            log.info("✅ FCM 토큰 조회 완료 - memberId: {}", memberId);
        }

        return tokenOpt;
    }

}
