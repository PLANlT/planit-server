package com.planit.planit.task.repository;

import com.planit.planit.member.Member;
import com.planit.planit.member.enums.SignType;
import com.planit.planit.member.repository.MemberRepository;
import com.planit.planit.member.enums.Role;
import com.planit.planit.plan.Plan;
import com.planit.planit.plan.enums.PlanStatus;
import com.planit.planit.plan.repository.PlanRepository;
import com.planit.planit.task.Task;
import com.planit.planit.task.association.CompletedTask;
import com.planit.planit.task.converter.RoutineConverter;
import com.planit.planit.task.enums.TaskType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private TaskRepository taskRepository;

    private Member member;
    private Plan plan;
    private Task task1;
    private Task task2;


    @BeforeEach
    public void setUp() {
        initMember();                   // 회원 더미데이터 생성
        initPlan();                     // 플랜 더미데이터 생성
        initTask();                     // 작업 더미데이터 생성
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
        plan = Plan.builder()
                .title("1")
                .motivation("다짐문장")
                .icon("아이콘")
                .planStatus(PlanStatus.IN_PROGRESS)
                .member(member)
                .build();
    }

    private void initTask() {
        task1 = Task.builder()
                .title("작업1")
                .member(member)
                .plan(plan)
                .build();
        task2 = Task.builder()
                .title("작업2")
                .member(member)
                .plan(plan)
                .build();

        CompletedTask completedTask = new CompletedTask(task1, LocalDate.now());
        task1.addCompletedTask(completedTask);
    }


    @Test
    @Order(1)
    @Transactional
    @DisplayName("작업 저장 테스트 (성공)")
    public void saveTest() {

        // given
        member = memberRepository.save(member);
        plan = planRepository.save(plan);

        // when
        Task result = taskRepository.save(task1);

        // then
        assertNotNull(result);
        assertThat(result.getTitle()).isEqualTo("작업1");
        assertThat(result.getTaskType()).isEqualTo(TaskType.ALL);
        assertThat(result.getMember()).isEqualTo(member);
        assertThat(result.getPlan()).isEqualTo(plan);
    }

    @Test
    @Order(2)
    @Transactional
    @DisplayName("작업 아이디로 작업 조회 테스트 (성공)")
    public void findByIdTest() {

        // given
        member = memberRepository.save(member);
        plan = planRepository.save(plan);
        task1 = taskRepository.save(task1);

        // when
        Task result = taskRepository.findById(task1.getId()).get();

        // then
        assertNotNull(result);
        assertThat(result.getTitle()).isEqualTo("작업1");
        assertThat(result.getTaskType()).isEqualTo(TaskType.ALL);
        assertThat(result.getMember()).isEqualTo(member);
        assertThat(result.getPlan()).isEqualTo(plan);
    }

    @Test
    @Order(3)
    @Transactional
    @DisplayName("플랜별 작업 목록 조회 테스트 (성공)")
    public void findAllTasksByMemberIdAndPlanIdTest() {

        // given
        member = memberRepository.save(member);
        plan = planRepository.save(plan);
        task1 = taskRepository.save(task1);
        task2 = taskRepository.save(task2);

        // when
        List<Task> result = taskRepository.findAllByMemberIdAndPlanId(member.getId(), plan.getId());

        // then
        assertNotNull(result);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0)).isEqualTo(task1);
        assertThat(result.get(1)).isEqualTo(task2);
    }

    @Test
    @Order(4)
    @Transactional
    @DisplayName("작업명 수정 테스트 (성공)")
    public void updateTaskTitleTest() {

        // given
        member = memberRepository.save(member);
        plan = planRepository.save(plan);
        task1 = taskRepository.save(task1);

        // when
        task1.updateTaskTitle("새로운 제목");

        // then
        Task result = taskRepository.findById(task1.getId()).get();
        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(task1.getId());
        assertThat(result.getTitle()).isEqualTo("새로운 제목");
    }

    @Test
    @Order(5)
    @Transactional
    @DisplayName("루틴 설정 테스트 (성공)")
    public void setRoutineTest() {

        // given
        member = memberRepository.save(member);
        plan = planRepository.save(plan);
        task1 = taskRepository.save(task1);

        // when
        task1.setRoutine(
                TaskType.SLOW,
                RoutineConverter.routineDaysToByte(List.of(DayOfWeek.WEDNESDAY)),
                LocalTime.of(13,0)
        );

        // then
        Task result = taskRepository.findById(task1.getId()).get();
        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(task1.getId());
        assertThat(result.getTaskType()).isEqualTo(TaskType.SLOW);
        assertThat(result.getRoutine()).isEqualTo(RoutineConverter.routineDaysToByte(List.of(DayOfWeek.WEDNESDAY)));
        assertThat(result.getRoutineTime()).isEqualTo(LocalTime.of(13, 0));
    }


    @Test
    @Order(6)
    @Transactional
    @DisplayName("작업 삭제 테스트 (성공)")
    public void deleteTaskTest() {

        // given
        member = memberRepository.save(member);
        plan = planRepository.save(plan);
        task1 = taskRepository.save(task1);

        // when
        task1.deleteTask();

        // then
        Task result = taskRepository.findById(task1.getId()).get();
        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(task1.getId());
        assertThat(result.getDeletedAt()).isNotNull();
    }

}