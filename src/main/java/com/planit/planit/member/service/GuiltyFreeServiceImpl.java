package com.planit.planit.member.service;

import com.planit.planit.common.api.member.MemberHandler;
import com.planit.planit.common.api.member.status.MemberErrorStatus;
import com.planit.planit.member.Member;
import com.planit.planit.member.enums.GuiltyFreeReason;
import com.planit.planit.member.repository.MemberRepository;
import com.planit.planit.web.dto.member.guiltyfree.GuiltyFreeResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GuiltyFreeServiceImpl implements GuiltyFreeService {

    private final MemberRepository memberRepository;

    @Override
    public GuiltyFreeResponseDTO.GuiltyFreeActivationDTO activateGuiltyFree(Long memberId, GuiltyFreeReason reason) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));

        if (member.getGuiltyFree().getActive().plusDays(7).isAfter(LocalDate.now())) {
            throw new MemberHandler(MemberErrorStatus.GUILTY_FREE_ACTIVATION_FORBIDDEN);
        }

        // 길티프리 활성화
        member.activateGuiltyFree(reason, LocalDate.now());
        return GuiltyFreeResponseDTO.GuiltyFreeActivationDTO.of(member);
    }

    @Override
    public GuiltyFreeResponseDTO.GuiltyFreeActivationDTO getGuiltyFreeStatus(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));
        return GuiltyFreeResponseDTO.GuiltyFreeActivationDTO.of(member);
    }
}
