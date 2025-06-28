package com.planit.planit.task.service;

import com.planit.planit.web.dto.task.TaskRequestDTO;
import com.planit.planit.web.dto.task.TaskResponseDTO;

public class TaskCommandServiceImpl implements TaskCommandService {

    @Override
    public TaskResponseDTO.TaskPreviewDTO createTask(Long memberId, Long planId, String title) {
        return null;
    }

    @Override
    public TaskResponseDTO.TaskPreviewDTO updateTaskTitle(Long memberId, Long planId, Long taskId, String title) {
        return null;
    }

    @Override
    public TaskResponseDTO.TaskRoutineDTO setRoutine(Long memberId, Long planId, Long taskId, TaskRequestDTO.RoutineDTO routineDTO) {
        return null;
    }

    @Override
    public TaskResponseDTO.TaskPreviewDTO deleteTask(Long memberId, Long planId, Long taskId) {
        return null;
    }

    @Override
    public TaskResponseDTO.CompletedTaskDTO completeTask(Long memberId, Long planId, Long taskId) {
        return null;
    }

    @Override
    public TaskResponseDTO.CompletedTaskDTO cancelTaskCompletion(Long memberId, Long planId, Long taskId) {
        return null;
    }
}
