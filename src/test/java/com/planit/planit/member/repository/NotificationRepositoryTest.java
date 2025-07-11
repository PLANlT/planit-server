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
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class NotificationRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private NotificationRepository notificationRepository;

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

        em.persist(member);
        em.flush();
        em.clear();

        // when
        Optional<Notification> result = notificationRepository.findByMember(member);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getMember().getId()).isEqualTo(member.getId());
        assertThat(result.get().isDailyTaskEnabled()).isTrue();
    }
}
