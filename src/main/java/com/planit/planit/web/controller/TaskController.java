package com.planit.planit.web.controller;

import com.planit.planit.auth.jwt.UserPrincipal;
import com.planit.planit.common.api.ApiResponse;
import com.planit.planit.common.api.task.status.TaskSuccessStatus;
import com.planit.planit.task.service.TaskCommandService;
import com.planit.planit.task.service.TaskQueryService;
import com.planit.planit.web.dto.task.TaskRequestDTO;
import com.planit.planit.web.dto.task.TaskResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import com.planit.planit.common.api.ApiErrorCodeExample;

@RestController
@RequiredArgsConstructor
@RequestMapping("/planit")
@Tag(name = "TASK", description = "작업 관련 API")
public class TaskController {

    private final TaskQueryService taskQueryService;
    private final TaskCommandService taskCommandService;

    @Operation(summary = "[TASK] 작업 생성하기")
    @ApiErrorCodeExample(value = com.planit.planit.common.api.member.status.MemberErrorStatus.class, codes = {"MEMBER_NOT_FOUND"})
    @ApiErrorCodeExample(value = com.planit.planit.common.api.plan.status.PlanErrorStatus.class, codes = {"PLAN_NOT_FOUND", "MEMBER_PLAN_NOT_FOUND"})
    @PostMapping("/tasks")
    public ResponseEntity<ApiResponse<TaskResponseDTO.TaskPreviewDTO>> createTask(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam Long planId,
            @RequestParam String title
    ) {
        TaskResponseDTO.TaskPreviewDTO taskPreviewDTO = taskCommandService.createTask(principal.getId(), planId, title);
        return ApiResponse.onSuccess(TaskSuccessStatus.TASK_CREATED, taskPreviewDTO);
    }

    @Operation(summary = "[TASK] 작업명 수정하기")
    @ApiErrorCodeExample(value = com.planit.planit.common.api.member.status.MemberErrorStatus.class, codes = {"MEMBER_NOT_FOUND"})
    @ApiErrorCodeExample(value = com.planit.planit.common.api.task.status.TaskErrorStatus.class, codes = {"TASK_NOT_FOUND", "MEMBER_TASK_NOT_FOUND", "TASK_DELETED"})
    @PatchMapping("/tasks/{taskId}/title")
    public ResponseEntity<ApiResponse<TaskResponseDTO.TaskPreviewDTO>> updateTask(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long taskId,
            @RequestParam String title
    ) {
        TaskResponseDTO.TaskPreviewDTO taskPreviewDTO = taskCommandService
                .updateTaskTitle(principal.getId(), taskId, title);
        return ApiResponse.onSuccess(TaskSuccessStatus.TASK_TITLE_UPDATED, taskPreviewDTO);
    }

    @Operation(summary = "[TASK] 루틴 설정하기")
    @ApiErrorCodeExample(value = com.planit.planit.common.api.member.status.MemberErrorStatus.class, codes = {"MEMBER_NOT_FOUND"})
    @ApiErrorCodeExample(value = com.planit.planit.common.api.task.status.TaskErrorStatus.class, codes = {"TASK_NOT_FOUND", "MEMBER_TASK_NOT_FOUND", "TASK_DELETED"})
    @PatchMapping("/tasks/{taskId}/routine")
    public ResponseEntity<ApiResponse<TaskResponseDTO.TaskRoutineDTO>> setRoutine(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long taskId,
            @RequestBody TaskRequestDTO.RoutineDTO routineDTO
    ) {
        TaskResponseDTO.TaskRoutineDTO taskRoutineDTO = taskCommandService
                .setRoutine(principal.getId(), taskId, routineDTO);
        return ApiResponse.onSuccess(TaskSuccessStatus.TASK_ROUTINE_SET, taskRoutineDTO);
    }

    @Operation(summary = "[TASK] 작업 삭제하기")
    @ApiErrorCodeExample(value = com.planit.planit.common.api.member.status.MemberErrorStatus.class, codes = {"MEMBER_NOT_FOUND"})
    @ApiErrorCodeExample(value = com.planit.planit.common.api.task.status.TaskErrorStatus.class, codes = {"TASK_NOT_FOUND", "MEMBER_TASK_NOT_FOUND", "TASK_DELETED"})
    @PatchMapping("/tasks/{taskId}/delete")
    public ResponseEntity<ApiResponse<TaskResponseDTO.TaskPreviewDTO>> deleteTask(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long taskId
    ) {
        TaskResponseDTO.TaskPreviewDTO taskPreviewDTO = taskCommandService.deleteTask(principal.getId(), taskId);
        return ApiResponse.onSuccess(TaskSuccessStatus.TASK_DELETED, taskPreviewDTO);
    }

    @Operation(summary = "[TASK] 작업 완료하기")
    @ApiErrorCodeExample(value = com.planit.planit.common.api.member.status.MemberErrorStatus.class, codes = {"MEMBER_NOT_FOUND"})
    @ApiErrorCodeExample(value = com.planit.planit.common.api.task.status.TaskErrorStatus.class, codes = {"TASK_NOT_FOUND", "MEMBER_TASK_NOT_FOUND", "TASK_DELETED", "NOT_ROUTINE_OF_TODAY", "TASK_ALREADY_COMPLETED"})
    @PostMapping("/tasks/{taskId}/complete")
    public ResponseEntity<ApiResponse<TaskResponseDTO.CompletedTaskDTO>> completeTask(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long taskId
    ) {
        LocalDate today = LocalDate.now();
        TaskResponseDTO.CompletedTaskDTO completedTaskDTO = taskCommandService
                .completeTask(principal.getId(), taskId, today);
        return ApiResponse.onSuccess(TaskSuccessStatus.TASK_COMPLETED, completedTaskDTO);
    }

    @Operation(summary = "[TASK] 작업 완료 취소하기")
    @ApiErrorCodeExample(value = com.planit.planit.common.api.member.status.MemberErrorStatus.class, codes = {"MEMBER_NOT_FOUND"})
    @ApiErrorCodeExample(value = com.planit.planit.common.api.task.status.TaskErrorStatus.class, codes = {"TASK_NOT_FOUND", "MEMBER_TASK_NOT_FOUND", "TASK_DELETED", "TASK_NOT_COMPLETED"})
    @PatchMapping("/tasks/{taskId}/cancel-completion")
    public ResponseEntity<ApiResponse<TaskResponseDTO.CompletedTaskDTO>> cancelTaskCompletion(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long taskId
    ) {
        LocalDate today = LocalDate.now();
        TaskResponseDTO.CompletedTaskDTO completedTaskDTO = taskCommandService
                .cancelTaskCompletion(principal.getId(), taskId, today);
        return ApiResponse.onSuccess(TaskSuccessStatus.TASK_COMPLETION_CANCELED, completedTaskDTO);
    }

    @Operation(summary = "[TASK] 루틴 조회하기")
    @ApiErrorCodeExample(value = com.planit.planit.common.api.member.status.MemberErrorStatus.class, codes = {"MEMBER_NOT_FOUND"})
    @ApiErrorCodeExample(value = com.planit.planit.common.api.task.status.TaskErrorStatus.class, codes = {"TASK_NOT_FOUND", "MEMBER_TASK_NOT_FOUND", "TASK_DELETED"})
    @GetMapping("/tasks/{taskId}/routine")
    public ResponseEntity<ApiResponse<TaskResponseDTO.TaskRoutineDTO>> getCurrentRoutine(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long taskId
    ) {
        TaskResponseDTO.TaskRoutineDTO taskRoutineDTO = taskQueryService.getCurrentRoutine(principal.getId(), taskId);
        return ApiResponse.onSuccess(TaskSuccessStatus.TASK_ROUTINE_FOUND, taskRoutineDTO);
    }
}
