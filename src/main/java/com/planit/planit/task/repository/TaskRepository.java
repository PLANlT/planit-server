package com.planit.planit.task.repository;

import com.planit.planit.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByMemberIdAndPlanId(Long memberId, Long planId);
}
