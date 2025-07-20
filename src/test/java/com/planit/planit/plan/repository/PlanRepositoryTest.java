package com.planit.planit.plan.repository;

import com.planit.planit.member.Member;
import com.planit.planit.member.enums.SignType;
import com.planit.planit.member.repository.MemberRepository;
import com.planit.planit.member.enums.Role;
import com.planit.planit.plan.Plan;
import com.planit.planit.plan.enums.PlanStatus;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PlanRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PlanRepository planRepository;

    private Member member;
    private Plan planInProgress1;
    private Plan planInProgress2;
    private Plan pausedPlan1;
    private Plan pausedPlan2;
    private Plan archivedPlan1;
    private Plan archivedPlan2;

    @BeforeEach
    public void beforeEach() {
        initMember();               // 회원 더미데이터 생성
        initPlan();                 // 플랜 더미데이터 생성
    }

    private void initMember() {
        member = Member.builder()
                .email("xxx@email.com")
                .password("password")
                .guiltyFreeMode(false)
                .memberName("xxx")
                .role(Role.USER)
                .signType(SignType.GOOGLE)
                .build();
    }

    private void initPlan() {
        planInProgress1 = Plan.of("1", "다짐문장", "아이콘",
                                  PlanStatus.IN_PROGRESS, null, null, member);
        planInProgress2 = Plan.of("2", "다짐문장", "아이콘",
                                  PlanStatus.IN_PROGRESS, null, null, member);
        pausedPlan1 = Plan.of("3", "다짐문장", "아이콘",
                              PlanStatus.PAUSED, null, LocalDate.now(), member);
        pausedPlan2 = Plan.of("4", "다짐문장", "아이콘",
                              PlanStatus.PAUSED, null, LocalDate.now().plusDays(1), member);
        archivedPlan1 = Plan.of("5", "다짐문장", "아이콘",
                                PlanStatus.ARCHIVED, null, LocalDate.now(), member);
        archivedPlan2 = Plan.of("6", "다짐문장", "아이콘",
                                PlanStatus.ARCHIVED, null, LocalDate.now().plusDays(1), member);
    }

    @Test
    @Order(1)
    @Transactional
    @DisplayName("플랜 저장 테스트 (성공)")
    public void saveTest() {

        // given
        member = memberRepository.save(member);

        // when
        Plan result = planRepository.save(planInProgress1);

        // then
        assertNotNull(result);
        assertThat(result.getTitle()).isEqualTo("1");
        assertThat(result.getPlanStatus()).isEqualTo(PlanStatus.IN_PROGRESS);
        assertThat(result.getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    @Order(2)
    @Transactional(readOnly = true)
    @DisplayName("플랜 아이디로 플랜 조회 테스트 (성공)")
    public void findByIdTest() {

        // given
        member = memberRepository.save(member);
        planInProgress1 = planRepository.save(planInProgress1);

        // when
        Plan result = planRepository.findById(planInProgress1.getId()).get();

        // then
        assertNotNull(result);
        assertThat(result.getTitle()).isEqualTo("1");
        assertThat(result.getPlanStatus()).isEqualTo(PlanStatus.IN_PROGRESS);
        assertThat(result.getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    @Order(3)
    @Transactional(readOnly = true)
    @DisplayName("진행중인 플랜 목록 조회 테스트 (성공)")
    public void findAllByMemberIdAndPlanStatusTest_InProgress() {

        // given
        member = memberRepository.save(member);
        planInProgress1 = planRepository.save(planInProgress1);
        planInProgress2 = planRepository.save(planInProgress2);

        // when
        List<Plan> result = planRepository.findAllByMemberIdAndPlanStatus(member.getId(), PlanStatus.IN_PROGRESS);

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getTitle()).isEqualTo("1");
        assertThat(result.get(1).getTitle()).isEqualTo("2");
    }

    @Test
    @Order(4)
    @Transactional(readOnly = true)
    @DisplayName("중단된 플랜 목록 조회 테스트 (성공)")
    public void findAllByMemberIdAndPlanStatusTest_Paused() {

        // given
        member = memberRepository.save(member);
        pausedPlan1 = planRepository.save(pausedPlan1);
        pausedPlan2 = planRepository.save(pausedPlan2);

        // when
        List<Plan> result = planRepository.findAllByMemberIdAndPlanStatus(member.getId(), PlanStatus.PAUSED);

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getTitle()).isEqualTo("3");
        assertThat(result.get(1).getTitle()).isEqualTo("4");
    }

    @Test
    @Order(5)
    @Transactional(readOnly = true)
    @DisplayName("아카이빙된 플랜 목록 조회 테스트 (성공)")
    public void findAllByMemberIdAndPlanStatusTest_Archived() {

        // given
        member = memberRepository.save(member);
        archivedPlan1 = planRepository.save(archivedPlan1);
        archivedPlan2 = planRepository.save(archivedPlan2);

        // when
        List<Plan> result = planRepository.findAllByMemberIdAndPlanStatus(member.getId(), PlanStatus.ARCHIVED);

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getTitle()).isEqualTo("5");
        assertThat(result.get(1).getTitle()).isEqualTo("6");
    }

    @Test
    @Order(6)
    @Transactional
    @DisplayName("플랜 아카이빙 테스트 (성공)")
    public void completePlanTest() {

        // given
        member = memberRepository.save(member);
        planInProgress1 = planRepository.save(planInProgress1);

        // when
        planInProgress1.completePlan();

        // then
        Plan result = planRepository.findById(planInProgress1.getId()).get();
        assertNotNull(result);
        assertThat(result.getTitle()).isEqualTo("1");
        assertThat(result.getPlanStatus()).isEqualTo(PlanStatus.ARCHIVED);
        assertThat(result.getInactive()).isNotNull();
    }

    @Test
    @Order(7)
    @Transactional
    @DisplayName("플랜 중단 테스트 (성공)")
    public void pausePlanTest() {

        // given
        member = memberRepository.save(member);
        planInProgress1 = planRepository.save(planInProgress1);

        // when
        planInProgress1.pausePlan();

        // then
        Plan result = planRepository.findById(planInProgress1.getId()).get();
        assertNotNull(result);
        assertThat(result.getTitle()).isEqualTo("1");
        assertThat(result.getPlanStatus()).isEqualTo(PlanStatus.PAUSED);
        assertThat(result.getInactive()).isNotNull();
    }

    @Test
    @Order(8)
    @Transactional
    @DisplayName("플랜 삭제 테스트 (성공)")
    public void deletePlanTest() {

        // given
        member = memberRepository.save(member);
        planInProgress1 = planRepository.save(planInProgress1);

        // when
        planInProgress1.deletePlan();

        // then
        Plan result = planRepository.findById(planInProgress1.getId()).get();
        assertNotNull(result);
        assertThat(result.getTitle()).isEqualTo("1");
        assertThat(result.getPlanStatus()).isEqualTo(PlanStatus.DELETED);
        assertThat(result.getInactive()).isNotNull();
    }
}