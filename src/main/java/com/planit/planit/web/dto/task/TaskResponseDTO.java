package com.planit.planit.web.dto.task;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TaskResponseDTO {

    @Getter
    public static class TaskPreviewDTO {
        private final Long taskId;
        private final String title;
        private final Boolean isCompleted;

        @Builder
        public TaskPreviewDTO(Long taskId, String title, Boolean isCompleted) {
            this.taskId = taskId;
            this.title = title;
            this.isCompleted = isCompleted;
        }
    }
}
