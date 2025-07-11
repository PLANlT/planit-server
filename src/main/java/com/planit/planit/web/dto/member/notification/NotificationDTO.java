package com.planit.planit.web.dto.member.notification;

import lombok.*;

public class NotificationDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private boolean dailyTaskEnabled;
        private boolean guiltyFreeEnabled;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToggleRequest {
        private boolean enabled;
    }
}
