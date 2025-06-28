package com.planit.planit.task.service;

import com.planit.planit.web.dto.task.TaskRequestDTO;
import com.planit.planit.web.dto.task.TaskResponseDTO;

public class TaskCommandServiceImpl implements TaskCommandService {

    @Override
    public TaskResponseDTO.TaskPreviewDTO createTask(Long planId) {
        return null;
    }

    @Override
    public TaskResponseDTO.TaskPreviewDTO updateTaskTitle(Long planId, Long taskId, String title) {
        return null;
    }

    @Override
    public TaskResponseDTO.TaskRoutineDTO setRoutine(Long planId, Long taskId, TaskRequestDTO.RoutineDTO routineDTO) {
        return null;
    }

    @Override
    public TaskResponseDTO.TaskPreviewDTO deleteTask(Long planId, Long taskId) {
        return null;
    }

    @Override
    public TaskResponseDTO.TaskStatusDTO completeTask(Long planId, Long taskId) {
        return null;
    }

    @Override
    public TaskResponseDTO.TaskStatusDTO cancelTaskCompletion(Long planId, Long taskId) {
        return null;
    }
}
