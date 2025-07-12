package com.planit.planit.member.repository;

import com.planit.planit.member.Member;
import com.planit.planit.member.association.FcmToken;
import com.planit.planit.member.enums.DailyCondition;
import com.planit.planit.member.enums.Role;
import com.planit.planit.member.enums.SignType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FcmTokenRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("FcmToken을 저장하고 조회할 수 있다")
    void saveAndFindFcmToken() {
        // given - 먼저 Member를 영속화
        Member member = Member.builder()
                .email("test@planit.com")
                .password("test1234")
                .signType(SignType.GOOGLE)
                .memberName("푸바오")
                .role(Role.USER)
                .guiltyFreeMode(false)
                .dailyCondition(DailyCondition.DISTRESS)
                .build();

        Member savedMember = memberRepository.save(member);
        em.flush(); // ID를 즉시 반영해줌

        // FcmToken 생성 (MapsId를 위해 반드시 member.getId() 필요)
        FcmToken token = FcmToken.of(savedMember, "fcm_test_token_123");
        FcmToken savedToken = fcmTokenRepository.save(token);

        // when
        Optional<FcmToken> foundById = fcmTokenRepository.findById(savedToken.getMemberId());
        Optional<FcmToken> foundByToken = fcmTokenRepository.findByToken("fcm_test_token_123");

        // then
        assertThat(foundById).isPresent();
        assertThat(foundByToken).isPresent();
        assertThat(foundById.get().getToken()).isEqualTo("fcm_test_token_123");
    }

    @Test
    @DisplayName("존재하지 않는 토큰은 조회되지 않는다")
    void findNonExistentToken() {
        // when
        Optional<FcmToken> result = fcmTokenRepository.findByToken("nonexistent_token");

        // then
        assertThat(result).isEmpty();
    }
}
