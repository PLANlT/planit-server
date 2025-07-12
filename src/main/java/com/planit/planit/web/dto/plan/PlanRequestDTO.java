package com.planit.planit.web.dto.plan;

import com.planit.planit.plan.enums.PlanStatus;
import lombok.Getter;
import lombok.Builder;
import org.springframework.util.Assert;

import java.time.LocalDate;

public class PlanRequestDTO {

    @Getter
    public static class PlanDTO {
        private final String title;
        private final String motivation;
        private final String icon;
        private final PlanStatus planStatus;
        private final LocalDate startedAt;
        private final LocalDate finishedAt;

        @Builder
        public PlanDTO(String title, String motivation, String icon,
                       PlanStatus planStatus, LocalDate startedAt, LocalDate finishedAt
        ) {
            validate(title, icon, planStatus);
            this.title = title;
            this.motivation = motivation;
            this.icon = icon;
            this.planStatus = planStatus;
            this.startedAt = startedAt;
            this.finishedAt = finishedAt;
        }

        private void validate(String title, String icon, PlanStatus planStatus) {
            Assert.notNull(title, "Title must not be null");
            Assert.notNull(icon, "Icon must not be null");
            Assert.notNull(planStatus, "PlanStatus must not be null");
        }
    }
}
