package com.planit.planit.web.dto.plan.converter;

import com.planit.planit.plan.Plan;
import com.planit.planit.plan.enums.PlanStatus;
import com.planit.planit.web.dto.plan.PlanRequestDTO;
import com.planit.planit.web.dto.plan.PlanResponseDTO;
import com.planit.planit.web.dto.task.converter.TaskConverter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class PlanConverter {

    public static PlanRequestDTO.PlanDTO toPlanDTO(Plan plan) {
        return PlanRequestDTO.PlanDTO.builder()
                .title(plan.getTitle())
                .motivation(plan.getMotivation())
                .icon(plan.getIcon())
                .planStatus(plan.getPlanStatus())
                .startedAt(plan.getStartedAt())
                .finishedAt(plan.getFinishedAt())
                .build();
    }

    public static PlanResponseDTO.PlanPreviewDTO toPlanPreviewDTO(Plan plan) {
        String dDay = formatDDay(LocalDateTime.now(), plan.getFinishedAt());
        return PlanResponseDTO.PlanPreviewDTO.builder()
                .planId(plan.getId())
                .title(plan.getTitle())
                .icon(plan.getIcon())
                .totalTasks(plan.countTasks())
                .dDay(dDay)
                .build();
    }

    public static PlanResponseDTO.PlanContentDTO toPlanDetailDTO(Plan plan) {
        return PlanResponseDTO.PlanContentDTO.builder()
                .planId(plan.getId())
                .title(plan.getTitle())
                .icon(plan.getIcon())
                .motivation(plan.getMotivation())
                .tasks(plan.getTasks().stream()
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

    public static PlanResponseDTO.PlanListDTO toPlanListDTO(PlanStatus planStatus, List<Plan> plans) {
        return PlanResponseDTO.PlanListDTO.builder()
                .planStatus(planStatus)
                .plans(plans.stream()
                        .map(PlanConverter::toPlanPreviewDTO)
                        .toList())
                .build();
    }

    private static String formatDDay(LocalDateTime startedAt, LocalDateTime finishedAt) {
        Long day = ChronoUnit.DAYS.between(startedAt, finishedAt);
        if (startedAt.isAfter(finishedAt)) {
            return String.format("D+%d", -day);
        } else {
            return String.format("D-%d", day);
        }
    }
}
