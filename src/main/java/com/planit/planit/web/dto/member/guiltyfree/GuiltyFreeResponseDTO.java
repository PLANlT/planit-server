package com.planit.planit.web.dto.member.guiltyfree;

import com.planit.planit.member.Member;
import com.planit.planit.member.association.GuiltyFree;
import com.planit.planit.member.association.GuiltyFreeProperty;
import com.planit.planit.member.enums.GuiltyFreeReason;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
public class GuiltyFreeResponseDTO {

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GuiltyFreeActivationDTO {
        private final String memberName;
        private final LocalDateTime activatedAt;
        private final String advice;

        public static GuiltyFreeActivationDTO of(Member member, @Nullable String advice) {
            return new GuiltyFreeActivationDTO(
                    member.getMemberName(),
                    LocalDateTime.of(member.getLastGuiltyFreeDate(), LocalTime.MIN),
                    advice
            );
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GuiltyFreeStatusDTO {
        private final String memberName;
        private final LocalDateTime activatedAt;

        public static GuiltyFreeStatusDTO of(Member member) {
            return new GuiltyFreeStatusDTO(
                    member.getMemberName(),
                    LocalDateTime.of(member.getLastGuiltyFreeDate(), LocalTime.MIN)
            );
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GuiltyFreeReasonListDTO {
        private final String memberName;
        private final List<GuiltyFreeReasonDTO> guiltyFreeReasons;

        public static GuiltyFreeReasonListDTO of(Member member) {
            return new GuiltyFreeReasonListDTO(member.getMemberName(),
                    member.getGuiltyFrees().stream()
                            .filter(guiltyFree -> guiltyFree.getActive().isAfter(GuiltyFreeProperty.guiltyFreeInitDate))
                            .map(GuiltyFreeReasonDTO::of)
                            .toList()
            );
        }

    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GuiltyFreeReasonDTO {
        private final LocalDate activatedAt;
        private final GuiltyFreeReason reason;

        public static GuiltyFreeReasonDTO of(GuiltyFree guiltyFree) {
            return new GuiltyFreeReasonDTO(guiltyFree.getActive(), guiltyFree.getReason());
        }
    }
}
