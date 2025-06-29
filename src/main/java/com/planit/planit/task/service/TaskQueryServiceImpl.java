package com.planit.planit.task.service;

import com.planit.planit.web.dto.task.TaskResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskQueryServiceImpl implements TaskQueryService {

    @Override
    public TaskResponseDTO.TaskRoutineDTO getCurrentRoutine(Long memberId, Long planId, Long taskId) {
        return null;
    }
}
