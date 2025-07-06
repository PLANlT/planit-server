package com.planit.planit.plan.repository;

import com.planit.planit.plan.Plan;
import com.planit.planit.plan.enums.PlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    List<Plan> findAllByMemberIdAndPlanStatus(Long memberId, PlanStatus planStatus);
}
