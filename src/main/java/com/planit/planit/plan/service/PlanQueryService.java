package com.planit.planit.plan.service;

import com.planit.planit.plan.enums.PlanStatus;
import com.planit.planit.web.dto.plan.PlanResponseDTO;

public interface PlanQueryService {

    // 오늘의 플랜 목록 조회하기
    PlanResponseDTO.TodayPlanListDTO getTodayPlans(Long memberId);

    // 플랜 단건 조회하기
    PlanResponseDTO.PlanContentDTO getPlan(Long memberId, Long planId);

    // 플랜 목록 조회하기
    PlanResponseDTO.PlanListDTO getPlansByPlanStatus(Long memberId, PlanStatus planStatus);

}
