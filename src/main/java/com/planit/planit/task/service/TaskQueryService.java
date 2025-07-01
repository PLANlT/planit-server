package com.planit.planit.task.service;

import com.planit.planit.web.dto.task.TaskResponseDTO;

public interface TaskQueryService {

    // 루틴 조회하기
    TaskResponseDTO.TaskRoutineDTO getCurrentRoutine(Long memberId, Long planId, Long taskId);
}
