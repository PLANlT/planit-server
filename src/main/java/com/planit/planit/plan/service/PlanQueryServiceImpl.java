package com.planit.planit.plan.service;

import com.planit.planit.plan.enums.PlanStatus;
import com.planit.planit.web.dto.plan.PlanResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class PlanQueryServiceImpl implements PlanQueryService {

    @Override
    public PlanResponseDTO.PlanContentDTO getPlan(Long planId) {
        return null;
    }

    @Override
    public PlanResponseDTO.PlanListDTO getPlansByPlanStatus(PlanStatus planStatus) {
        return null;
    }
}
