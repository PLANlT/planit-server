package com.planit.planit.task.service;

import com.planit.planit.common.api.member.MemberHandler;
import com.planit.planit.common.api.member.status.MemberErrorStatus;
import com.planit.planit.common.api.plan.PlanHandler;
import com.planit.planit.common.api.plan.status.PlanErrorStatus;
import com.planit.planit.member.Member;
import com.planit.planit.member.MemberRepository;
import com.planit.planit.plan.Plan;
import com.planit.planit.plan.repository.PlanRepository;
import com.planit.planit.task.Task;
import com.planit.planit.task.repository.CompletedTaskRepository;
import com.planit.planit.task.repository.TaskRepository;
import com.planit.planit.web.dto.task.TaskRequestDTO;
import com.planit.planit.web.dto.task.TaskResponseDTO;
import com.planit.planit.web.dto.task.converter.TaskConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskCommandServiceImpl implements TaskCommandService {

    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;
    private final TaskRepository taskRepository;
    private final CompletedTaskRepository completedTaskRepository;

    @Override
    public TaskResponseDTO.TaskPreviewDTO createTask(Long memberId, Long planId, String title) {
        return null;
    }

    @Override
    public TaskResponseDTO.TaskPreviewDTO updateTaskTitle(Long memberId, Long taskId, String title) {
        return null;
    }

    @Override
    public TaskResponseDTO.TaskRoutineDTO setRoutine(Long memberId, Long taskId, TaskRequestDTO.RoutineDTO routineDTO) {
        return null;
    }

    @Override
    public TaskResponseDTO.TaskPreviewDTO deleteTask(Long memberId, Long taskId) {
        return null;
    }

    @Override
    public TaskResponseDTO.CompletedTaskDTO completeTask(Long memberId, Long taskId) {
        return null;
    }

    @Override
    public TaskResponseDTO.CompletedTaskDTO cancelTaskCompletion(Long memberId, Long taskId) {
        return null;
    }
}
