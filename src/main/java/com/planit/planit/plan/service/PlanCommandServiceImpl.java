package com.planit.planit.plan.service;

import com.planit.planit.common.api.member.MemberHandler;
import com.planit.planit.common.api.member.status.MemberErrorStatus;
import com.planit.planit.common.api.plan.PlanHandler;
import com.planit.planit.common.api.plan.status.PlanErrorStatus;
import com.planit.planit.member.Member;
import com.planit.planit.member.repository.MemberRepository;
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
        member.addPlan(plan);

        return PlanConverter.toPlanMetaDTO(plan);
    }


    @Override
    public PlanResponseDTO.PlanMetaDTO updatePlan(Long memberId, Long planId, PlanRequestDTO.PlanDTO planDTO) {

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new PlanHandler(PlanErrorStatus.PLAN_NOT_FOUND));

        // 로그인한 회원의 플랜인지 확인
        if (!memberId.equals(plan.getMember().getId())) {
            throw new PlanHandler(PlanErrorStatus.MEMBER_PLAN_NOT_FOUND);
        }

        // 삭제된 플랜인지 확인
        if (plan.getPlanStatus().equals(PlanStatus.DELETED)) {
            throw new PlanHandler(PlanErrorStatus.PLAN_DELETED);
        }

        plan.updatePlan(
                planDTO.getTitle(),
                planDTO.getMotivation(),
                planDTO.getIcon(),
                planDTO.getPlanStatus(),
                planDTO.getStartedAt(),
                planDTO.getFinishedAt()
        );

        return PlanConverter.toPlanMetaDTO(plan);
    }


    @Override
    public PlanResponseDTO.PlanMetaDTO completePlan(Long memberId, Long planId) {

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new PlanHandler(PlanErrorStatus.PLAN_NOT_FOUND));

        // 로그인한 회원의 플랜인지 확인
        if (!memberId.equals(plan.getMember().getId())) {
            throw new PlanHandler(PlanErrorStatus.MEMBER_PLAN_NOT_FOUND);
        }

        // 삭제된 플랜인지 확인
        if (plan.getPlanStatus().equals(PlanStatus.DELETED)) {
            throw new PlanHandler(PlanErrorStatus.PLAN_DELETED);
        }

        plan.completePlan();

        return PlanConverter.toPlanMetaDTO(plan);
    }


    @Override
    public PlanResponseDTO.PlanMetaDTO pausePlan(Long memberId, Long planId) {

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new PlanHandler(PlanErrorStatus.PLAN_NOT_FOUND));

        // 로그인한 회원의 플랜인지 확인
        if (!memberId.equals(plan.getMember().getId())) {
            throw new PlanHandler(PlanErrorStatus.MEMBER_PLAN_NOT_FOUND);
        }

        // 삭제된 플랜인지 확인
        if (plan.getPlanStatus().equals(PlanStatus.DELETED)) {
            throw new PlanHandler(PlanErrorStatus.PLAN_DELETED);
        }

        plan.pausePlan();

        return PlanConverter.toPlanMetaDTO(plan);
    }


    @Override
    public PlanResponseDTO.PlanMetaDTO deletePlan(Long memberId, Long planId) {

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new PlanHandler(PlanErrorStatus.PLAN_NOT_FOUND));

        // 로그인한 회원의 플랜인지 확인
        if (!memberId.equals(plan.getMember().getId())) {
            throw new PlanHandler(PlanErrorStatus.MEMBER_PLAN_NOT_FOUND);
        }

        plan.deletePlan();

        return PlanConverter.toPlanMetaDTO(plan);
    }
}
