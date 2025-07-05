package com.planit.planit.member.service;

import com.planit.planit.member.enums.GuiltyFreeReason;
import com.planit.planit.web.dto.member.guiltyfree.GuiltyFreeResponseDTO;

public interface GuiltyFreeService {

    GuiltyFreeResponseDTO.GuiltyFreeActivationDTO activateGuiltyFree(Long memberId, GuiltyFreeReason reason);

    GuiltyFreeResponseDTO.GuiltyFreeStatusDTO getGuiltyFreeStatus(Long memberId);
}
