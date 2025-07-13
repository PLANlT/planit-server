package com.planit.planit.member.service;

import com.planit.planit.web.dto.member.notification.NotificationDTO;

public interface NotificationService {
    void updateDailyTaskNotification(Long memberId, NotificationDTO.ToggleRequest toggleRequest);
    void updateGuiltFreeNotification(Long memberId, NotificationDTO.ToggleRequest toggleRequest);
    NotificationDTO.Response getMyNotificationSetting(Long memberId);
}
