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

        validatePlanStatus(planDTO.getPlanStatus());

        Plan plan = Plan.of(planDTO.getTitle(), planDTO.getMotivation(), planDTO.getIcon(), planDTO.getPlanStatus(),
                            planDTO.getStartedAt(), planDTO.getFinishedAt(), member);

        plan = planRepository.save(plan);
        member.addPlan(plan);

        return PlanConverter.toPlanMetaDTO(plan);
    }


    @Override
    public PlanResponseDTO.PlanMetaDTO updatePlan(Long memberId, Long planId, PlanRequestDTO.PlanDTO planDTO) {

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new PlanHandler(PlanErrorStatus.PLAN_NOT_FOUND));

        validatePlanStatus(planDTO.getPlanStatus());
        validateMemberPlan(memberId, plan);
        validateNotDeletedPlan(plan);

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

        validateMemberPlan(memberId, plan);
        validateNotDeletedPlan(plan);

        plan.completePlan();

        return PlanConverter.toPlanMetaDTO(plan);
    }


    @Override
    public PlanResponseDTO.PlanMetaDTO pausePlan(Long memberId, Long planId) {

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new PlanHandler(PlanErrorStatus.PLAN_NOT_FOUND));

        validateMemberPlan(memberId, plan);
        validateNotDeletedPlan(plan);

        plan.pausePlan();

        return PlanConverter.toPlanMetaDTO(plan);
    }


    @Override
    public PlanResponseDTO.PlanMetaDTO deletePlan(Long memberId, Long planId) {

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new PlanHandler(PlanErrorStatus.PLAN_NOT_FOUND));

        validateMemberPlan(memberId, plan);

        plan.deletePlan();

        return PlanConverter.toPlanMetaDTO(plan);
    }


    @Override
    public PlanResponseDTO.PlanMetaDTO restartArchive(Long memberId, Long planId) {

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new PlanHandler(PlanErrorStatus.PLAN_NOT_FOUND));

        // 로그인한 회원의 플랜인지 확인
        if (!memberId.equals(plan.getMember().getId())) {
            throw new PlanHandler(PlanErrorStatus.MEMBER_PLAN_NOT_FOUND);
        }

        plan.restartArchive();

        return PlanConverter.toPlanMetaDTO(plan);
    }


    /**
     * 요청한 플랜 상태가 IN_PROGRESS거나 PAUSED임을 검증하는 메소드
     * @param planStatus 요청한 플랜 상태
     */
    private void validatePlanStatus(PlanStatus planStatus) {
        if (!(planStatus.equals(PlanStatus.IN_PROGRESS) ||
                planStatus.equals(PlanStatus.PAUSED))
        ) {
            throw new PlanHandler(PlanErrorStatus.INVALID_PLAN_STATUS);
        }
    }

    /**
     * 삭제되지 않은 플랜임을 검증하는 메소드
     * @param plan 타겟 플랜
     */
    private static void validateNotDeletedPlan(Plan plan) {
        if (plan.getPlanStatus().equals(PlanStatus.DELETED)) {
            throw new PlanHandler(PlanErrorStatus.PLAN_DELETED);
        }
    }

    /**
     * 로그인한 사용자의 플랜임을 검증하는 메소드
     * @param memberId 사용자 아이디
     * @param plan 타겟 플랜
     */
    private static void validateMemberPlan(Long memberId, Plan plan) {
        if (!memberId.equals(plan.getMember().getId())) {
            throw new PlanHandler(PlanErrorStatus.MEMBER_PLAN_NOT_FOUND);
        }
    }
}
