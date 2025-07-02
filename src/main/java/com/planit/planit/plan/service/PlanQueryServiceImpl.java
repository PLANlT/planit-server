package com.planit.planit.plan.service;

import com.planit.planit.common.api.member.MemberHandler;
import com.planit.planit.common.api.member.status.MemberErrorStatus;
import com.planit.planit.common.api.plan.PlanHandler;
import com.planit.planit.common.api.plan.status.PlanErrorStatus;
import com.planit.planit.member.MemberRepository;
import com.planit.planit.plan.Plan;
import com.planit.planit.plan.enums.PlanStatus;
import com.planit.planit.plan.repository.PlanRepository;
import com.planit.planit.web.dto.plan.PlanResponseDTO;
import com.planit.planit.web.dto.plan.converter.PlanConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlanQueryServiceImpl implements PlanQueryService {

    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;

    @Override
    public PlanResponseDTO.TodayPlanListDTO getTodayPlans(Long memberId) {

        LocalDate todayDate = LocalDate.now();

        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));

        List<Plan> todayPlans = planRepository
                .findAllByMemberIdAndPlanStatus(memberId, PlanStatus.IN_PROGRESS).stream()
                .filter(plan -> plan.getFinishedAt().isAfter(todayDate) ||
                                     plan.getFinishedAt().isEqual(todayDate))
                .toList();

        return PlanConverter.toTodayPlanListDTO(todayDate, todayPlans);
    }

    @Override
    public PlanResponseDTO.PlanContentDTO getPlan(Long memberId, Long planId) {

        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));

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

        return PlanConverter.toPlanContentDTO(plan);
    }

    @Override
    public PlanResponseDTO.PlanListDTO getPlansByPlanStatus(Long memberId, PlanStatus planStatus) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));

        List<Plan> plans = planRepository.findAllByMemberIdAndPlanStatus(memberId, planStatus);

        return PlanConverter.toPlanListDTO(planStatus, plans);
    }
}
