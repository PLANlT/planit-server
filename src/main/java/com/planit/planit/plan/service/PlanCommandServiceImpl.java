package com.planit.planit.plan.service;

import com.planit.planit.common.api.member.MemberHandler;
import com.planit.planit.common.api.member.status.MemberErrorStatus;
import com.planit.planit.member.Member;
import com.planit.planit.member.MemberRepository;
import com.planit.planit.plan.Plan;
import com.planit.planit.plan.enums.PlanStatus;
import com.planit.planit.plan.repository.PlanRepository;
import com.planit.planit.web.dto.plan.PlanRequestDTO;
import com.planit.planit.web.dto.plan.PlanResponseDTO;
import com.planit.planit.web.dto.plan.converter.PlanConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanCommandServiceImpl implements PlanCommandService {

    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;

    @Override
    public PlanResponseDTO.PlanMetaDTO createPlan(Long memberId, PlanRequestDTO.PlanDTO planDTO) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));

        Plan plan = Plan.builder()
                .title(planDTO.getTitle())
                .motivation(planDTO.getMotivation())
                .icon(planDTO.getIcon())
                .planStatus(PlanStatus.IN_PROGRESS)
                .startedAt(planDTO.getStartedAt())
                .finishedAt(planDTO.getFinishedAt())
                .member(member)
                .build();

        plan = planRepository.save(plan);

        return PlanConverter.toPlanMetaDTO(plan);
    }

    @Override
    public PlanResponseDTO.PlanMetaDTO updatePlan(Long memberId, Long planId, PlanRequestDTO.PlanDTO planDTO) {
        return null;
    }

    @Override
    public PlanResponseDTO.PlanMetaDTO completePlan(Long memberId, Long planId) {
        return null;
    }

    @Override
    public PlanResponseDTO.PlanMetaDTO pausePlan(Long memberId, Long planId) {
        return null;
    }

    @Override
    public PlanResponseDTO.PlanMetaDTO deletePlan(Long memberId, Long planId) {
        return null;
    }
}
