package com.planit.planit.web.controller;

import com.planit.planit.auth.jwt.UserPrincipal;
import com.planit.planit.common.api.ApiResponse;
import com.planit.planit.common.api.member.status.MemberSuccessStatus;
import com.planit.planit.member.enums.GuiltyFreeReason;
import com.planit.planit.member.service.GuiltyFreeService;
import com.planit.planit.web.dto.member.guiltyfree.GuiltyFreeResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/planit")
@Tag(name = "GUILTY-FREE", description = "길티프리 관련 API")
public class GuiltyFreeController {

    private final GuiltyFreeService guiltyFreeService;

    @Operation(summary = "[GUILTY-FREE] 길티프리 활성화하기")
    @PatchMapping("/guilty-free")
    public ResponseEntity<ApiResponse<GuiltyFreeResponseDTO.GuiltyFreeActivationDTO>> activateGuiltyFree(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam GuiltyFreeReason reason
    ) {
        GuiltyFreeResponseDTO.GuiltyFreeActivationDTO guiltyFreeActivationDTO = guiltyFreeService
                .activateGuiltyFree(principal.getId(), reason);
        return ApiResponse.onSuccess(MemberSuccessStatus.GUILTY_FREE_SET, guiltyFreeActivationDTO);
    }

    @Operation(summary = "[GUILTY-FREE] 길티프리 활성일 조회하기")
    @GetMapping("/guilty-free")
    public ResponseEntity<ApiResponse<GuiltyFreeResponseDTO.GuiltyFreeStatusDTO>> getGuiltyFreeStatus(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        GuiltyFreeResponseDTO.GuiltyFreeStatusDTO guiltyFreeActivationDTO = guiltyFreeService
                .getGuiltyFreeStatus(principal.getId());
        return ApiResponse.onSuccess(MemberSuccessStatus.GUILTY_FREE_FOUND, guiltyFreeActivationDTO);
    }

    @Operation(summary = "[GUILTY-FREE] 길티프리 사유 목록 조회하기")
    @GetMapping("/guilty-free/list")
    public ResponseEntity<ApiResponse<GuiltyFreeResponseDTO.GuiltyFreeReasonListDTO>> getGuiltyFreeReasons(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        GuiltyFreeResponseDTO.GuiltyFreeReasonListDTO guiltyFreeReasonListDTO = guiltyFreeService
                .getGuiltyFreeReasons(principal.getId());
        return ApiResponse.onSuccess(MemberSuccessStatus.GUILTY_FREE_REASON_LIST_FOUND, guiltyFreeReasonListDTO);
    }
}
        