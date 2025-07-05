package com.planit.planit.web.controller;

import com.planit.planit.common.api.ApiResponse;
import com.planit.planit.common.api.member.status.MemberSuccessStatus;
import com.planit.planit.member.enums.GuiltyFreeReason;
import com.planit.planit.member.service.GuiltyFreeService;
import com.planit.planit.web.dto.member.guiltyfree.GuiltyFreeResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/planit")
@Tag(name = "GUILTY-FREE", description = "길티프리 관련 API")
public class GuiltyFreeController {

    private final GuiltyFreeService guiltyFreeService;

    @Operation(summary = "[GUILTY-FREE] 길티프리 활성화하기")
    @PatchMapping("/guilty-free")
    public ApiResponse<GuiltyFreeResponseDTO.GuiltyFreeActivationDTO> activateGuiltyFree(
            @RequestParam GuiltyFreeReason reason
    ) {
        Long memberId = 1L; // 인증 기능 구현 이후 변경
        GuiltyFreeResponseDTO.GuiltyFreeActivationDTO guiltyFreeActivationDTO = guiltyFreeService.activateGuiltyFree(memberId, reason);
        return ApiResponse.onSuccess(MemberSuccessStatus.GUILTY_FREE_SET, guiltyFreeActivationDTO);
    }

    @Operation(summary = "[GUILTY-FREE] 길티프리 활성일 조회하기")
    @GetMapping("/guilty-free")
    public ApiResponse<GuiltyFreeResponseDTO.GuiltyFreeActivationDTO> getGuiltyFreeStatus() {
        Long memberId = 1L; // 인증 기능 구현 이후 변경
        GuiltyFreeResponseDTO.GuiltyFreeActivationDTO guiltyFreeActivationDTO = guiltyFreeService.getGuiltyFreeStatus(memberId);
        return ApiResponse.onSuccess(MemberSuccessStatus.GUILTY_FREE_FOUND, guiltyFreeActivationDTO);
    }
}
