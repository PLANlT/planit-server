package com.planit.planit.web.dto.member.guiltyfree;

import com.planit.planit.member.Member;
import com.planit.planit.member.association.GuiltyFree;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class GuiltyFreeResponseDTO {

    @Getter
    public static class GuiltyFreeActivationDTO {
        private final String memberName;
        private final LocalDateTime activatedAt;

        private GuiltyFreeActivationDTO(String memberName, LocalDateTime activatedAt) {
            this.memberName = memberName;
            this.activatedAt = activatedAt;
        }

        public static GuiltyFreeActivationDTO of(Member member) {
            return new GuiltyFreeActivationDTO(
                    member.getMemberName(),
                    LocalDateTime.of(member.getGuiltyFree().getActive(), LocalTime.MIN)
            );
        }
    }
}
