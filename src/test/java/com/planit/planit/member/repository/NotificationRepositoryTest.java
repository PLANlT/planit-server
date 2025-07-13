package com.planit.planit.member.repository;

import com.planit.planit.member.Member;
import com.planit.planit.member.association.Notification;
import com.planit.planit.member.enums.DailyCondition;
import com.planit.planit.member.enums.Role;
import com.planit.planit.member.enums.SignType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원으로 알림 설정을 조회할 수 있다")
    void findByMember_success() {
        // given
        Member member = Member.builder()
                .email("test@planit.com")
                .password("password")
                .signType(SignType.GOOGLE)
                .memberName("홍길동")
                .role(Role.USER)
                .guiltyFreeMode(false)
                .dailyCondition(DailyCondition.DISTRESS)
                .build();

        Member savedMember = memberRepository.save(member);

        // Notification 생성 및 저장
        Notification notification = Notification.of(savedMember);
        notificationRepository.save(notification);

        // when
        Optional<Notification> result = notificationRepository.findByMember(savedMember);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getMember().getId()).isEqualTo(savedMember.getId());
        assertThat(result.get().isDailyTaskEnabled()).isTrue();
    }
}
