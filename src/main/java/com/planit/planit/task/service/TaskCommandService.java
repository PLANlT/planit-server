package com.planit.planit.task.service;

import com.planit.planit.web.dto.task.TaskRequestDTO;
import com.planit.planit.web.dto.task.TaskResponseDTO;

import java.time.LocalDate;

public interface TaskCommandService {

    // 작업 생성하기
    TaskResponseDTO.TaskPreviewDTO createTask(Long memberId, Long planId, TaskRequestDTO.TaskCreateDTO taskCreateDTO);

    // 작업명 수정하기
    TaskResponseDTO.TaskPreviewDTO updateTaskTitle(Long memberId, Long taskId, String title);

    // 루틴 설정하기
    TaskResponseDTO.TaskRoutineDTO setRoutine(Long memberId, Long taskId, TaskRequestDTO.RoutineDTO routineDTO);

    // 작업 삭제하기
    TaskResponseDTO.TaskPreviewDTO deleteTask(Long memberId, Long taskId);

    // 작업 완료하기
    TaskResponseDTO.CompletedTaskDTO completeTask(Long memberId, Long taskId, LocalDate today);

    // 작업 완료 취소하기
    TaskResponseDTO.CompletedTaskDTO cancelTaskCompletion(Long memberId, Long taskId, LocalDate today);


}
