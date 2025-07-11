package com.planit.planit.member.service;

import com.planit.planit.member.Member;
import com.planit.planit.member.association.Notification;
import com.planit.planit.member.enums.DailyCondition;
import com.planit.planit.member.enums.Role;
import com.planit.planit.member.enums.SignType;
import com.planit.planit.member.repository.MemberRepository;
import com.planit.planit.member.repository.NotificationRepository;
import com.planit.planit.web.dto.member.notification.NotificationDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Test
    @DisplayName("알림 설정을 정상적으로 조회한다")
    void getMyNotificationSetting_success() {
        // given
        Long memberId = 1L; // 조회할 회원 ID
        Member member = createDummyMember();
        Notification notification = Notification.of(member);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(notificationRepository.findByMember(member)).willReturn(Optional.of(notification));

        // when
        NotificationDTO.Response result = notificationService.getMyNotificationSetting(memberId);

        // then
        assertThat(result.isDailyTaskEnabled()).isTrue();
        assertThat(result.isGuiltyFreeEnabled()).isTrue();
    }

    @Test
    @DisplayName("알림 설정을 정상적으로 수정한다")
    void updateMyNotificationSetting_success() {
        // given
        Member member = createDummyMember();
        Notification notification = Notification.of(member);
        NotificationDTO.ToggleRequest updateRequest = new NotificationDTO.ToggleRequest(false);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(notificationRepository.findByMember(member)).willReturn(Optional.of(notification));

        // when
        notificationService.updateGuiltFreeNotification(1L, updateRequest);
        notificationService.updateDailyTaskNotification(1L, updateRequest);

        // then
        assertThat(notification.isDailyTaskEnabled()).isFalse();
        assertThat(notification.isGuiltyFreeEnabled()).isFalse();

        verify(memberRepository, times(2)).findById(1L);
        verify(notificationRepository, times(2)).findByMember(member);
    }

    private Member createDummyMember() {
        return Member.builder()
                .id(1L)
                .email("test@planit.com")
                .password("pw")
                .signType(SignType.GOOGLE)
                .memberName("홍길동")
                .guiltyFreeMode(true)
                .dailyCondition(DailyCondition.WELL_BEING)
                .role(Role.USER)
                .build();
    }
}
