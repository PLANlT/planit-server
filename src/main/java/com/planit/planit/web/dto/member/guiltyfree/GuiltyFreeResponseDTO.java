package com.planit.planit.web.dto.member.guiltyfree;

import com.planit.planit.member.Member;
import com.planit.planit.member.association.GuiltyFree;
import jakarta.annotation.Nullable;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class GuiltyFreeResponseDTO {

    @Getter
    public static class GuiltyFreeActivationDTO {
        private final String memberName;
        private final LocalDateTime activatedAt;
        private final String advice;

        private GuiltyFreeActivationDTO(String memberName, LocalDateTime activatedAt, String advice) {
            this.memberName = memberName;
            this.activatedAt = activatedAt;
            this.advice = advice;
        }

        public static GuiltyFreeActivationDTO of(Member member, @Nullable String advice) {
            return new GuiltyFreeActivationDTO(
                    member.getMemberName(),
                    LocalDateTime.of(member.getLastGuiltyFreeDate(), LocalTime.MIN),
                    advice
            );
        }
    }

    @Getter
    public static class GuiltyFreeStatusDTO {
        private final String memberName;
        private final LocalDateTime activatedAt;

        private GuiltyFreeStatusDTO(String memberName, LocalDateTime activatedAt) {
            this.memberName = memberName;
            this.activatedAt = activatedAt;
        }

        public static GuiltyFreeStatusDTO of(Member member) {
            return new GuiltyFreeStatusDTO(
                    member.getMemberName(),
                    LocalDateTime.of(member.getLastGuiltyFreeDate(), LocalTime.MIN)
            );
        }

    }
}
