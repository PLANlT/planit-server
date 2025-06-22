package com.planit.planit.task.repository;

import com.planit.planit.common.api.general.GeneralException;
import com.planit.planit.common.api.general.status.ErrorStatus;
import com.planit.planit.member.Member;
import com.planit.planit.member.MemberRepository;
import com.planit.planit.plan.Plan;
import com.planit.planit.plan.enums.PlanStatus;
import com.planit.planit.plan.repository.PlanRepository;
import com.planit.planit.task.Task;
import com.planit.planit.task.association.CompletedTask;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CompletedTaskRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private CompletedTaskRepository completedTaskRepository;

    private Member member;
    private Plan plan;
    private Task task;
    private CompletedTask completedTask1;
    private CompletedTask completedTask2;


    @BeforeEach
    public void setUp() {
        initMember();                   // 회원 더미데이터 생성
        initPlan();                     // 플랜 더미데이터 생성
        initTask();                     // 작업 더미데이터 생성
        initCompletedTask();            // 완료된 작업 더미데이터 생성
    }

    private void initMember() {
        member = Member.builder()
                .email("xxx@email.com")
                .password("password")
                .guiltyFreeMode(false)
                .build();
    }

    private void initPlan() {
        plan = Plan.builder()
                .title("1")
                .motivation("다짐문장")
                .icon("아이콘")
                .planStatus(PlanStatus.IN_PROGRESS)
                .member(member)
                .build();
    }

    private void initTask() {
        task = Task.builder()
                .title("작업1")
                .member(member)
                .plan(plan)
                .build();
    }

    private void initCompletedTask() {
        completedTask1 = new CompletedTask(task, LocalDate.now().minusDays(5));
        completedTask2 = new CompletedTask(task, LocalDate.now());
        task.addCompletedTask(completedTask1);
        task.addCompletedTask(completedTask2);
    }

    @Test
    @Order(1)
    @Transactional
    @DisplayName("완료된 작업 저장 테스트 (성공)")
    public void saveTest() {

        // given
        member = memberRepository.save(member);
        plan = planRepository.save(plan);
        task = taskRepository.save(task);

        // when
        CompletedTask result = completedTaskRepository.save(completedTask2);

        // then
        assertNotNull(result);
        assertThat(result.getTask()).isEqualTo(task);
        assertThat(result.getCompletedAt()).isEqualTo(LocalDate.now());

    }

    @Test
    @Order(2)
    @Transactional
    @DisplayName("아이디로 완료된 작업 조회 테스트 (성공)")
    public void findByIdTest() {

        // given
        member = memberRepository.save(member);
        plan = planRepository.save(plan);
        task = taskRepository.save(task);
        completedTask2 = completedTaskRepository.save(completedTask2);

        // when
        CompletedTask result = completedTaskRepository.findById(completedTask2.getId()).get();

        // then
        assertNotNull(result);
        assertThat(result.getTask()).isEqualTo(task);
        assertThat(result.getCompletedAt()).isEqualTo(LocalDate.now());
    }

    @Test
    @Order(3)
    @Transactional
    @DisplayName("아이디로 오늘 완료된 작업 조회 테스트 (성공)")
    public void findAllByTaskIdAndCompletedAtTest() {

        // given
        member = memberRepository.save(member);
        plan = planRepository.save(plan);
        task = taskRepository.save(task);
        completedTask1 = completedTaskRepository.save(completedTask1);
        completedTask2 = completedTaskRepository.save(completedTask2);

        // when
        List<CompletedTask> result = completedTaskRepository
                .findAllByTaskIdAndCompletedAt(task.getId(), LocalDate.now());

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getTask()).isEqualTo(task);
        assertThat(result.get(0).getCompletedAt()).isEqualTo(LocalDate.now());
    }

    @Test
    @Order(4)
    @Transactional
    @DisplayName("아이디로 작업 삭제 테스트 (성공)")
    public void deleteByIdTest() {

        // given
        member = memberRepository.save(member);
        plan = planRepository.save(plan);
        task = taskRepository.save(task);
        completedTask1 = completedTaskRepository.save(completedTask1);

        // when
        completedTaskRepository.deleteById(completedTask1.getId());

        // then
        assertThrows(GeneralException.class, () ->
                completedTaskRepository.findById(completedTask1.getId())
                        .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND)));
    }
}