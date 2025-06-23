package com.planit.planit.web.dto.task;

import com.planit.planit.task.enums.TaskType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
public class TaskRequestDTO {

    @Getter
    @Builder
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RoutineDTO {
        private final TaskType taskType;
        private final DayOfWeek routineDay;
        private final LocalTime routineTime;
    }
}
