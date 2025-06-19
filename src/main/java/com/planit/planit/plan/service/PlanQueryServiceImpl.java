package com.planit.planit.plan.service;

import com.planit.planit.common.api.member.MemberHandler;
import com.planit.planit.common.api.member.status.MemberErrorStatus;
import com.planit.planit.member.MemberRepository;
import com.planit.planit.plan.Plan;
import com.planit.planit.plan.enums.PlanStatus;
import com.planit.planit.plan.repository.PlanRepository;
import com.planit.planit.web.dto.plan.PlanResponseDTO;
import com.planit.planit.web.dto.plan.converter.PlanConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
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
        return null;
    }

    @Override
    public PlanResponseDTO.PlanListDTO getPlansByPlanStatus(Long memberId, PlanStatus planStatus) {
        return null;
    }
}
