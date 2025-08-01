package com.planit.planit.member.service;

import com.planit.planit.common.api.member.MemberHandler;
import com.planit.planit.common.api.member.status.MemberErrorStatus;
import com.planit.planit.member.Member;
import com.planit.planit.member.association.GuiltyFree;
import com.planit.planit.member.association.GuiltyFreeProperty;
import com.planit.planit.member.enums.GuiltyFreeReason;
import com.planit.planit.member.repository.GuiltyFreeRepository;
import com.planit.planit.member.repository.MemberRepository;
import com.planit.planit.web.dto.member.guiltyfree.GuiltyFreeResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class GuiltyFreeServiceImpl implements GuiltyFreeService {

    private final MemberRepository memberRepository;
    private final GuiltyFreeRepository guiltyFreeRepository;

    @Override
    public GuiltyFreeResponseDTO.GuiltyFreeActivationDTO activateGuiltyFree(Long memberId, GuiltyFreeReason reason) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));

        // 주 1회만 길티프리 활성화 가능
        if (member.getLastGuiltyFreeDate().plusDays(7).isAfter(LocalDate.now())) {
            throw new MemberHandler(MemberErrorStatus.GUILTY_FREE_ACTIVATION_FORBIDDEN);
        }

        // 길티프리 활성화
        GuiltyFree guiltyFree = guiltyFreeRepository.save(GuiltyFree.of(member, reason, LocalDate.now()));
        member.activateGuiltyFree(guiltyFree);

        return GuiltyFreeResponseDTO.GuiltyFreeActivationDTO.of(member);
    }

    @Override
    public GuiltyFreeResponseDTO.GuiltyFreeStatusDTO getGuiltyFreeStatus(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));
        // 길티프리를 한 번도 사용한 적 없는 경우
        if (member.getLastGuiltyFreeDate().equals(GuiltyFreeProperty.guiltyFreeInitDate)) {
            throw new MemberHandler(MemberErrorStatus.GUILTY_FREE_NOT_ACTIVATED);
        }
        return GuiltyFreeResponseDTO.GuiltyFreeStatusDTO.of(member);
    }

    @Override
    public GuiltyFreeResponseDTO.GuiltyFreeReasonListDTO getGuiltyFreeReasons(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));
        return GuiltyFreeResponseDTO.GuiltyFreeReasonListDTO.of(member);
    }
}
