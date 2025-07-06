package com.planit.planit.web.dto.plan;

import com.planit.planit.plan.enums.PlanStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
public class PlanRequestDTO {

    @Getter
    @Builder
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PlanDTO {
        private final String title;
        private final String motivation;
        private final String icon;
        private final PlanStatus planStatus;
        private final LocalDate startedAt;
        private final LocalDate finishedAt;
    }
}
