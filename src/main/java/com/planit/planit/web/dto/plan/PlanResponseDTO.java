package com.planit.planit.web.dto.plan;

import com.planit.planit.plan.enums.PlanStatus;
import com.planit.planit.web.dto.task.TaskResponseDTO.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PlanResponseDTO {

    @Getter
    @Builder
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PlanPreviewDTO {
        private final Long planId;
        private final String title;
        private final String icon;
        private final String motivation;
        private final Integer totalTasks;
        private final String dDay;
    }

    @Getter
    @Builder
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PlanContentDTO {
        private final Long planId;
        private final String title;
        private final String icon;
        private final String motivation;
        private final List<TaskPreviewDTO> tasks;
    }

    @Getter
    @Builder
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TodayPlanDTO {
        private final Long planId;
        private final String title;
        private final String dDay;
        private final List<TaskStatusDTO> tasks;
    }

    @Getter
    @Builder
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PlanMetaDTO {
        private final Long planId;
        private final String title;
        private final String icon;
        private final String motivation;
        private final PlanStatus planStatus;
        private final LocalDate startedAt;
        private final LocalDate finishedAt;
    }

    @Getter
    @Builder
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TodayPlanListDTO {
        private final LocalDate todayDate;
        private final DayOfWeek dayOfWeek;
        private final List<TodayPlanDTO> plans;

    }

    @Getter
    @Builder
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PlanListDTO {
        private final PlanStatus planStatus;
        private final List<PlanPreviewDTO> plans;
    }
}
