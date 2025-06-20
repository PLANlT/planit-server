package com.planit.planit.web.dto.task;

import com.planit.planit.task.enums.TaskType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class TaskResponseDTO {

    @Getter
    @Builder
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TaskPreviewDTO {
        private final Long taskId;
        private final TaskType taskType;
        private final String title;
    }
}
