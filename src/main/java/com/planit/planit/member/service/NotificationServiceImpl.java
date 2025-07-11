package com.planit.planit.member.service;

import com.planit.planit.common.api.member.MemberHandler;
import com.planit.planit.common.api.member.status.MemberErrorStatus;
import com.planit.planit.member.Member;
import com.planit.planit.member.association.Notification;
import com.planit.planit.member.repository.MemberRepository;
import com.planit.planit.member.repository.NotificationRepository;
import com.planit.planit.web.dto.member.notification.NotificationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public void updateDailyTaskNotification(Long memberId, NotificationDTO.ToggleRequest toggleRequest) {
        Notification notification = getNotification(memberId);
        boolean enabled = toggleRequest.isEnabled();
        notification.updateDailyTask(enabled);
    }

    @Override
    @Transactional
    public void updateGuiltFreeNotification(Long memberId, NotificationDTO.ToggleRequest toggleRequest) {
        Notification notification = getNotification(memberId);
        boolean enabled = toggleRequest.isEnabled();
        notification.updateGuiltFree(enabled);
    }

    @Transactional(readOnly = true)
    public Notification getNotification(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));
        return notificationRepository.findByMember(member)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.NOTIFICATION_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    @Override
    public NotificationDTO.Response getMyNotificationSetting(Long memberId) {
        Notification notification = getNotification(memberId);

        return NotificationDTO.Response.builder()
                .dailyTaskEnabled(notification.isDailyTaskEnabled())
                .guiltyFreeEnabled(notification.isGuiltyFreeEnabled())
                .build();
    }
}
