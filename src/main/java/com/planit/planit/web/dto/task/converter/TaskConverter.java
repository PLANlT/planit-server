package com.planit.planit.web.dto.task.converter;

import com.planit.planit.task.Task;
import com.planit.planit.web.dto.task.TaskResponseDTO;

public class TaskConverter {

    public static TaskResponseDTO.TaskPreviewDTO toTaskPreviewDTO(Task task) {
        return TaskResponseDTO.TaskPreviewDTO.builder()
                .taskId(task.getId())
                .taskType(task.getTaskType())
                .title(task.getTitle())
                .isCompleted(task.getIsCompleted())
                .build();
    }
}
