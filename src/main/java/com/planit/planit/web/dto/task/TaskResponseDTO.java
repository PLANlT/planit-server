package com.planit.planit.web.dto.task;

import com.planit.planit.task.enums.TaskType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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

    @Getter
    @Builder
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TaskRoutineDTO {
        private final Long taskId;
        private final TaskType taskType;
        private final List<DayOfWeek> routineDay;
        private final LocalTime routineTime;
    }

    @Getter
    @Builder
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TaskStatusDTO {
        private final Long taskId;
        private final String title;
        private final LocalTime routineTime;
        private final Boolean isCompleted;
    }

    @Getter
    @Builder
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CompletedTaskDTO {
        private final Long taskId;
        private final String title;
        private final LocalDate date;
        private final Boolean isCompleted;
    }
}
