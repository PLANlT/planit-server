package com.planit.planit.web.dto.plan.converter;

import com.planit.planit.plan.Plan;
import com.planit.planit.plan.enums.PlanStatus;
import com.planit.planit.task.enums.TaskType;
import com.planit.planit.web.dto.plan.PlanRequestDTO;
import com.planit.planit.web.dto.plan.PlanResponseDTO;
import com.planit.planit.web.dto.task.converter.TaskConverter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class PlanConverter {

    public static PlanResponseDTO.PlanPreviewDTO toPlanPreviewDTO(Plan plan) {
        String dDay = plan.getFinishedAt() != null ? formatDDay(LocalDate.now(), plan.getFinishedAt()) : null;
        return PlanResponseDTO.PlanPreviewDTO.builder()
                .planId(plan.getId())
                .title(plan.getTitle())
                .icon(plan.getIcon())
                .motivation(plan.getMotivation())
                .totalTasks(plan.countTasks())
                .dDay(dDay)
                .build();
    }

    public static PlanResponseDTO.PlanContentDTO toPlanContentDTO(Plan plan) {
        return PlanResponseDTO.PlanContentDTO.builder()
                .planId(plan.getId())
                .title(plan.getTitle())
                .icon(plan.getIcon())
                .motivation(plan.getMotivation())
                .tasks(plan.getTasks().stream()
                        .filter(task -> task.getDeletedAt() == null)
                        .map(TaskConverter::toTaskPreviewDTO)
                        .toList())
                .build();
    }

    public static PlanResponseDTO.PlanMetaDTO toPlanMetaDTO(Plan plan) {
        return PlanResponseDTO.PlanMetaDTO.builder()
                .planId(plan.getId())
                .title(plan.getTitle())
                .icon(plan.getIcon())
                .motivation(plan.getMotivation())
                .planStatus(plan.getPlanStatus())
                .startedAt(plan.getStartedAt())
                .finishedAt(plan.getFinishedAt())
                .build();
    }

    public static PlanResponseDTO.TodayPlanDTO toTodayPlanDTO(Plan plan, TaskType taskType, LocalDate today) {
        String dDay = plan.getFinishedAt() != null ? formatDDay(LocalDate.now(), plan.getFinishedAt()) : null;
        return PlanResponseDTO.TodayPlanDTO.builder()
                .planId(plan.getId())
                .title(plan.getTitle())
                .dDay(dDay)
                .tasks(TaskConverter.toTodayTaskList(plan, taskType, today))
                .build();
    }

    public static PlanResponseDTO.TodayPlanListDTO toTodayPlanListDTO(LocalDate todayDate, List<Plan> plans) {
        return PlanResponseDTO.TodayPlanListDTO.builder()
                .todayDate(todayDate)
                .dayOfWeek(todayDate.getDayOfWeek())
                .slowPlans(plans.stream()
                        .map(plan -> PlanConverter.toTodayPlanDTO(plan, TaskType.SLOW, todayDate))
                        .filter(dto -> !dto.getTasks().isEmpty())
                        .toList())
                .passionatePlans(plans.stream()
                        .map(plan -> PlanConverter.toTodayPlanDTO(plan, TaskType.PASSIONATE, todayDate))
                        .filter(dto -> !dto.getTasks().isEmpty())
                        .toList())
                .build();
    }

    public static PlanResponseDTO.PlanListDTO toPlanListDTO(PlanStatus planStatus, List<Plan> plans) {
        return PlanResponseDTO.PlanListDTO.builder()
                .planStatus(planStatus)
                .plans(plans.stream()
                        .map(PlanConverter::toPlanPreviewDTO)
                        .toList())
                .build();
    }

    public static PlanResponseDTO.ArchiveListDTO toArchiveListDTO(List<Plan> plans) {
        return PlanResponseDTO.ArchiveListDTO.builder()
                .archives(plans.stream()
                        .map(PlanConverter::toArchiveDTO)
                        .toList())
                .build();
    }

    private static PlanResponseDTO.ArchiveDTO toArchiveDTO(Plan plan) {
        return PlanResponseDTO.ArchiveDTO.builder()
                .planId(plan.getId())
                .title(plan.getTitle())
                .icon(plan.getIcon())
                .motivation(plan.getMotivation())
                .progressDays(ChronoUnit.DAYS.between(plan.getStartedAt(), plan.getInactive().toLocalDate()))
                .completedDaysAgo(ChronoUnit.DAYS.between(plan.getInactive().toLocalDate(), LocalDate.now()))
                .build();
    }

    private static String formatDDay(LocalDate startedAt, LocalDate finishedAt) {
        Long day = ChronoUnit.DAYS.between(startedAt, finishedAt);
        if (startedAt.isAfter(finishedAt)) {
            return String.format("D+%d", -day);
        } else if (startedAt.isBefore(finishedAt)) {
            return String.format("D-%d", day);
        }
        else {
            return "D-Day";
        }
    }
}
