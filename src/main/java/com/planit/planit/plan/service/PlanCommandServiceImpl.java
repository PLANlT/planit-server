package com.planit.planit.plan.service;

import com.planit.planit.web.dto.plan.PlanRequestDTO;
import com.planit.planit.web.dto.plan.PlanResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class PlanCommandServiceImpl implements PlanCommandService {

    @Override
    public PlanResponseDTO.PlanMetaDTO createPlan(PlanRequestDTO.PlanDTO planDTO) {
        return null;
    }

    @Override
    public PlanResponseDTO.PlanMetaDTO updatePlan(Long planId, PlanRequestDTO.PlanDTO planDTO) {
        return null;
    }

    @Override
    public PlanResponseDTO.PlanMetaDTO completePlan(Long planId) {
        return null;
    }

    @Override
    public PlanResponseDTO.PlanMetaDTO pausePlan(Long planId) {
        return null;
    }

    @Override
    public PlanResponseDTO.PlanMetaDTO deletePlan(Long planId) {
        return null;
    }
}
