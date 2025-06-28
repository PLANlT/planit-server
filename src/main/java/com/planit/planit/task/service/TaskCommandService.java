package com.planit.planit.task.service;

import com.planit.planit.web.dto.task.TaskRequestDTO;
import com.planit.planit.web.dto.task.TaskResponseDTO;

public interface TaskCommandService {

    // 작업 생성하기
    TaskResponseDTO.TaskPreviewDTO createTask(Long planId);

    // 작업명 수정하기
    TaskResponseDTO.TaskPreviewDTO updateTaskTitle(Long planId, Long taskId, String title);

    // 루틴 설정하기
    TaskResponseDTO.TaskRoutineDTO setRoutine(Long planId, Long taskId, TaskRequestDTO.RoutineDTO routineDTO);

    // 작업 삭제하기
    TaskResponseDTO.TaskPreviewDTO deleteTask(Long planId, Long taskId);

    // 작업 완료하기
    TaskResponseDTO.TaskStatusDTO completeTask(Long planId, Long taskId);

    // 작업 완료 취소하기
    TaskResponseDTO.TaskStatusDTO cancelTaskCompletion(Long planId, Long taskId);
}
