package com.planit.planit.web.controller;

import com.planit.planit.auth.jwt.UserPrincipal;
import com.planit.planit.common.api.ApiResponse;
import com.planit.planit.common.api.plan.status.PlanSuccessStatus;
import com.planit.planit.plan.enums.PlanStatus;
import com.planit.planit.plan.service.PlanCommandService;
import com.planit.planit.plan.service.PlanQueryService;
import com.planit.planit.web.dto.plan.PlanRequestDTO;
import com.planit.planit.web.dto.plan.PlanResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.planit.planit.common.api.ApiErrorCodeExample;

@RestController
@RequiredArgsConstructor
@RequestMapping("/planit")
@Tag(name = "PLAN", description = "플랜 관련 API")
public class PlanController {

    private final PlanQueryService planQueryService;
    private final PlanCommandService planCommandService;

    @Operation(summary = "[PLAN] 플랜 생성하기")
    @ApiErrorCodeExample(value = com.planit.planit.common.api.member.status.MemberErrorStatus.class, codes = {"MEMBER_NOT_FOUND"})
    @PostMapping("/plans")
    public ResponseEntity<ApiResponse<PlanResponseDTO.PlanMetaDTO>> createPlan(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody PlanRequestDTO.PlanDTO planDTO
    ) {
        PlanResponseDTO.PlanMetaDTO planMetaDTO = planCommandService.createPlan(principal.getId(), planDTO);
        return ApiResponse.onSuccess(PlanSuccessStatus.PLAN_CREATED, planMetaDTO);
    }


    @Operation(summary = "[PLAN] 플랜 수정하기")
    @ApiErrorCodeExample(value = com.planit.planit.common.api.plan.status.PlanErrorStatus.class, codes = {"PLAN_NOT_FOUND", "MEMBER_PLAN_NOT_FOUND", "PLAN_DELETED"})
    @PatchMapping("/plans/{planId}")
    public ResponseEntity<ApiResponse<PlanResponseDTO.PlanMetaDTO>> updatePlan(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long planId,
            @RequestBody PlanRequestDTO.PlanDTO planDTO
    ) {
        PlanResponseDTO.PlanMetaDTO planMetaDTO = planCommandService.updatePlan(principal.getId(), planId, planDTO);
        return ApiResponse.onSuccess(PlanSuccessStatus.PLAN_UPDATED, planMetaDTO);
    }


    @Operation(summary = "[PLAN] 플랜 완료(아카이빙) 하기")
    @ApiErrorCodeExample(value = com.planit.planit.common.api.plan.status.PlanErrorStatus.class, codes = {"PLAN_NOT_FOUND", "MEMBER_PLAN_NOT_FOUND", "PLAN_DELETED"})
    @PatchMapping("/plans/{planId}/complete")
    public ResponseEntity<ApiResponse<PlanResponseDTO.PlanMetaDTO>> completePlan(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long planId
    ) {
        PlanResponseDTO.PlanMetaDTO planMetaDTO = planCommandService.completePlan(principal.getId(), planId);
        return ApiResponse.onSuccess(PlanSuccessStatus.PLAN_COMPLETED, planMetaDTO);
    }


    @Operation(summary = "[PLAN] 플랜 중단하기")
    @ApiErrorCodeExample(value = com.planit.planit.common.api.plan.status.PlanErrorStatus.class, codes = {"PLAN_NOT_FOUND", "MEMBER_PLAN_NOT_FOUND", "PLAN_DELETED"})
    @PatchMapping("/plans/{planId}/pause")
    public ResponseEntity<ApiResponse<PlanResponseDTO.PlanMetaDTO>> pausePlan(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long planId
    ) {
        PlanResponseDTO.PlanMetaDTO planMetaDTO = planCommandService.pausePlan(principal.getId(), planId);
        return ApiResponse.onSuccess(PlanSuccessStatus.PLAN_PAUSED, planMetaDTO);
    }


    @Operation(summary = "[PLAN] 플랜 삭제하기")
    @ApiErrorCodeExample(value = com.planit.planit.common.api.plan.status.PlanErrorStatus.class, codes = {"PLAN_NOT_FOUND", "MEMBER_PLAN_NOT_FOUND"})
    @PatchMapping("/plans/{planId}/delete")
    public ResponseEntity<ApiResponse<PlanResponseDTO.PlanMetaDTO>> deletePlan(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long planId
    ) {
        PlanResponseDTO.PlanMetaDTO planMetaDTO = planCommandService.deletePlan(principal.getId(), planId);
        return ApiResponse.onSuccess(PlanSuccessStatus.PLAN_DELETED, planMetaDTO);
    }


    @Operation(summary = "[PLAN] 오늘의 플랜 목록 조회하기")
    @ApiErrorCodeExample(value = com.planit.planit.common.api.member.status.MemberErrorStatus.class, codes = {"MEMBER_NOT_FOUND"})
    @GetMapping("/plans/today")
    public ResponseEntity<ApiResponse<PlanResponseDTO.TodayPlanListDTO>> getTodayPlans(@AuthenticationPrincipal UserPrincipal principal) {
        PlanResponseDTO.TodayPlanListDTO todayPlanListDTO = planQueryService.getTodayPlans(principal.getId());
        return ApiResponse.onSuccess(PlanSuccessStatus.TODAY_PLAN_LIST_FOUND, todayPlanListDTO);
    }


    @Operation(summary = "[PLAN] 플랜 목록 조회하기")
    @ApiErrorCodeExample(value = com.planit.planit.common.api.member.status.MemberErrorStatus.class, codes = {"MEMBER_NOT_FOUND"})
    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<PlanResponseDTO.PlanListDTO>> getPlans(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam PlanStatus planStatus
    ) {
        PlanResponseDTO.PlanListDTO planListDTO = planQueryService.getPlansByPlanStatus(principal.getId(), planStatus);
        return ApiResponse.onSuccess(PlanSuccessStatus.PLAN_LIST_FOUND, planListDTO);
    }

    @Operation(summary = "[PLAN] 플랜 단건 조회하기")
    @ApiErrorCodeExample(value = com.planit.planit.common.api.plan.status.PlanErrorStatus.class, codes = {"PLAN_NOT_FOUND", "MEMBER_PLAN_NOT_FOUND", "PLAN_DELETED"})
    @GetMapping("/plans/{planId}")
    public ResponseEntity<ApiResponse<PlanResponseDTO.PlanContentDTO>> getPlan(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long planId
    ) {
        PlanResponseDTO.PlanContentDTO planContentDTO = planQueryService.getPlan(principal.getId(), planId);
        return ApiResponse.onSuccess(PlanSuccessStatus.PLAN_FOUND, planContentDTO);
    }

    @Operation(summary = "[ARCHIVE] 아카이빙된 플랜 다시 시작하기")
    @PatchMapping("/archives/{planId}/restart")
    public ResponseEntity<ApiResponse<PlanResponseDTO.PlanMetaDTO>> restartArchive(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long planId
    ) {
        PlanResponseDTO.PlanMetaDTO planMetaDTO = planCommandService.restartArchive(principal.getId(), planId);
        return ApiResponse.onSuccess(PlanSuccessStatus.ARCHIVE_RESTARTED, planMetaDTO);
    }

    @Operation(summary = "[ARCHIVE] 아카이브 목록 조회하기")
    @GetMapping("/archives")
    public ResponseEntity<ApiResponse<PlanResponseDTO.ArchiveListDTO>> getArchives(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        PlanResponseDTO.ArchiveListDTO archiveListDTO = planQueryService.getArchives(principal.getId());
        return ApiResponse.onSuccess(PlanSuccessStatus.ARCHIVE_LIST_FOUND, archiveListDTO);
    }
}
