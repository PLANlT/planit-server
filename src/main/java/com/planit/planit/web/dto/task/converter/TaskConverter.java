package com.planit.planit.web.dto.task.converter;

import com.planit.planit.plan.Plan;
import com.planit.planit.task.Task;
import com.planit.planit.task.association.CompletedTask;
import com.planit.planit.web.dto.task.TaskResponseDTO;

import java.time.LocalDate;
import java.util.List;

public class TaskConverter {

    public static TaskResponseDTO.TaskPreviewDTO toTaskPreviewDTO(Task task) {
        return TaskResponseDTO.TaskPreviewDTO.builder()
                .taskId(task.getId())
                .taskType(task.getTaskType())
                .title(task.getTitle())
                .build();
    }

    public static TaskResponseDTO.TaskRoutineDTO toTaskRoutineDTO(Task task) {
        return TaskResponseDTO.TaskRoutineDTO.builder()
                .taskId(task.getId())
                .taskType(task.getTaskType())
                .routineDay(task.getRoutineDay())
                .routineTime(task.getRoutineTime())
                .build();
    }

    public static TaskResponseDTO.TodayTaskListDTO toTodayTaskListDTO(Plan plan, LocalDate today) {

        List<TaskResponseDTO.TaskStatusDTO> todayTasks = plan.getTasks().stream()
                // 플랜에서 오늘 루틴에 해당하는 작업만 필터링
                .filter(task -> task.getRoutineDay().equals(today.getDayOfWeek()))
                // 오늘 완료된 작업이 있으면 true로 표시
                .map(task -> {
                    List<Task> tasks = task.getCompletedTasks().stream()
                            .filter(completedTask -> completedTask.getCompletedAt().equals(today))
                            .map(CompletedTask::getTask)
                            .toList();
                    return toTaskStatusDTO(task, !tasks.isEmpty());
                })
                .toList();

        return TaskResponseDTO.TodayTaskListDTO.builder()
                .date(today)
                .dayOfWeek(today.getDayOfWeek())
                .build();
    }

    public static TaskResponseDTO.TaskStatusDTO toTaskStatusDTO(Task task, Boolean isCompleted) {
        return TaskResponseDTO.TaskStatusDTO.builder()
                .taskId(task.getId())
                .title(task.getTitle())
                .routineTime(task.getRoutineTime())
                .isCompleted(isCompleted)
                .build();
    }

    public static TaskResponseDTO.CompletedTaskDTO toCompletedTaskDTO(CompletedTask completedTask, Boolean isCompleted) {
        return TaskResponseDTO.CompletedTaskDTO.builder()
                .taskId(completedTask.getTask().getId())
                .title(completedTask.getTask().getTitle())
                .date(completedTask.getCompletedAt())
                .isCompleted(isCompleted)
                .build();
    }
}
