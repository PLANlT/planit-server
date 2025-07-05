package com.planit.planit.web.dto.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.planit.planit.task.enums.TaskType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Getter
public class TaskRequestDTO {

    @Getter
    @Builder
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RoutineDTO {
        private final TaskType taskType;
        private final List<DayOfWeek> routineDay;

        @Schema(type = "string", pattern = "HH:mm", example = "14:00")
        @JsonFormat(pattern = "HH:mm")
        private final LocalTime routineTime;
    }
}
