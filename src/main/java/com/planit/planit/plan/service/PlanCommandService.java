package com.planit.planit.plan.service;

import com.planit.planit.web.dto.plan.PlanRequestDTO;
import com.planit.planit.web.dto.plan.PlanResponseDTO;

public interface PlanCommandService {

    // 플랜 생성하기
    PlanResponseDTO.PlanMetaDTO createPlan(Long memberId, PlanRequestDTO.PlanDTO planDTO);

    // 플랜 수정하기
    PlanResponseDTO.PlanMetaDTO updatePlan(Long memberId, Long planId, PlanRequestDTO.PlanDTO planDTO);

    // 플랜 완료(아카이빙) 하기
    PlanResponseDTO.PlanMetaDTO completePlan(Long memberId, Long planId);

    // 플랜 중단하기
    PlanResponseDTO.PlanMetaDTO pausePlan(Long memberId, Long planId);

    // 플랜 삭제하기
    PlanResponseDTO.PlanMetaDTO deletePlan(Long memberId, Long planId);

    // 아카이브된 플랜 다시 시작하기
    PlanResponseDTO.PlanMetaDTO restartArchive(Long memberId, Long planId);

}
