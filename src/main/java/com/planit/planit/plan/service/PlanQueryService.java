package com.planit.planit.plan.service;

import com.planit.planit.plan.enums.PlanStatus;
import com.planit.planit.web.dto.plan.PlanResponseDTO;

public interface PlanQueryService {

    // 플랜 단건 조회하기
    PlanResponseDTO.PlanContentDTO getPlan(Long planId);

    // 플랜 목록 조회하기
    PlanResponseDTO.PlanListDTO getPlansByPlanStatus(PlanStatus planStatus);
}
