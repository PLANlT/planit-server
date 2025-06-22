package com.planit.planit.plan.service;

import com.planit.planit.common.api.plan.PlanHandler;
import com.planit.planit.member.Member;
import com.planit.planit.member.MemberRepository;
import com.planit.planit.plan.Plan;
import com.planit.planit.plan.enums.PlanStatus;
import com.planit.planit.plan.repository.PlanRepository;
import com.planit.planit.task.Task;
import com.planit.planit.task.repository.TaskRepository;
import com.planit.planit.task.association.CompletedTask;
import com.planit.planit.task.enums.RoutineDay;
import com.planit.planit.task.enums.TaskType;
import com.planit.planit.web.dto.plan.PlanResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PlanQueryServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private PlanQueryServiceImpl planQueryService;

    private Member member1;
    private Member member2;

    private Plan planInProgress1;
    private Plan planInProgress2;
    private Plan pausedPlan1;
    private Plan pausedPlan2;
    private Plan archivedPlan1;
    private Plan archivedPlan2;

    private Task task1;
    private Task task2;


    /*------------------------------ SETUP ------------------------------*/

    @BeforeEach
    public void setUp() {
        initMember();                   // 회원 더미데이터 생성
        initPlan();                     // 플랜 더미데이터 생성
        initTask();                     // 작업 더미데이터 생성
    }

    private void initMember() {
        member1 = Member.builder()
                .id(1L)
                .email("xxx@email.com")
                .password("password")
                .guiltyFreeMode(false)
                .build();
        member2 = Member.builder()
                .id(2L)
                .email("yyy@email.com")
                .password("password")
                .guiltyFreeMode(false)
                .build();
    }

    private void initPlan() {
        planInProgress1 = Plan.builder()
                .id(1L)
                .title("1")
                .motivation("다짐문장")
                .icon("아이콘")
                .planStatus(PlanStatus.IN_PROGRESS)
                .finishedAt(LocalDate.now())
                .member(member1)
                .build();
        planInProgress2 = Plan.builder()
                .id(2L)
                .title("2")
                .motivation("다짐문장")
                .icon("아이콘")
                .planStatus(PlanStatus.IN_PROGRESS)
                .finishedAt(LocalDate.now().plusDays(1))
                .member(member1)
                .build();
        pausedPlan1 = Plan.builder()
                .id(3L)
                .title("3")
                .motivation("다짐문장")
                .icon("아이콘")
                .planStatus(PlanStatus.PAUSED)
                .finishedAt(LocalDate.now())
                .member(member1)
                .build();
        pausedPlan2 = Plan.builder()
                .id(4L)
                .title("4")
                .motivation("다짐문장")
                .icon("아이콘")
                .planStatus(PlanStatus.PAUSED)
                .finishedAt(LocalDate.now().plusDays(1))
                .member(member1)
                .build();
        archivedPlan1 = Plan.builder()
                .id(5L)
                .title("5")
                .motivation("다짐문장")
                .icon("아이콘")
                .planStatus(PlanStatus.ARCHIVED)
                .finishedAt(LocalDate.now())
                .member(member1)
                .build();
        archivedPlan2 = Plan.builder()
                .id(6L)
                .title("6")
                .motivation("다짐문장")
                .icon("아이콘")
                .planStatus(PlanStatus.ARCHIVED)
                .finishedAt(LocalDate.now().minusDays(1))
                .member(member1)
                .build();
    }

    private void initTask() {
        task1 = Task.builder()
                .id(1L)
                .title("작업1")
                .member(member1)
                .plan(planInProgress1)
                .build();
        task2 = Task.builder()
                .id(2L)
                .title("작업2")
                .member(member1)
                .plan(planInProgress1)
                .build();
        CompletedTask completedTask = new CompletedTask(task2, LocalDate.now());

        planInProgress1.addTask(task1);
        planInProgress1.addTask(task2);
    }


/*------------------------------ 오늘의 플랜 목록 조회 ------------------------------*/

    @Test
    @DisplayName("오늘의 플랜 목록 조회 (성공)")
    public void getTodayPlanListTest_Success() {

        // given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));
        when(planRepository.findAllByMemberIdAndPlanStatus(1L, PlanStatus.IN_PROGRESS))
                .thenReturn(List.of(planInProgress1, planInProgress2));

        // when
        PlanResponseDTO.TodayPlanListDTO result = planQueryService.getTodayPlans(1L);

        // then
        assertNotNull(result);
        assertThat(result.getTodayDate()).isEqualTo(LocalDate.now());
        assertThat(result.getPlans().size()).isEqualTo(2);
        assertThat(result.getPlans().get(0).getTitle()).isEqualTo("1");
        assertThat(result.getPlans().get(0).getDDay()).isEqualTo("D-Day");
        assertThat(result.getPlans().get(1).getTitle()).isEqualTo("2");
        assertThat(result.getPlans().get(1).getDDay()).isEqualTo("D-1");
    }

/*------------------------------ 플랜 단건 조회 ------------------------------*/

    @Test
    @DisplayName("플랜 단건 조회 (성공)")
    public void getPlanTest_Success() {

        // given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));
        when(planRepository.findById(1L)).thenReturn(Optional.of(planInProgress1));

        // when
        PlanResponseDTO.PlanContentDTO result = planQueryService.getPlan(1L, 1L);

        // then
        assertNotNull(result);
        assertThat(result.getPlanId()).isEqualTo(1L);
        assertThat(result.getTasks().get(0).getTaskId()).isEqualTo(1L);
        assertThat(result.getTasks().get(1).getTaskId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("플랜 단건 조회 (실패, 없는 플랜)")
    public void getPlanTest_NoPlan_Fail() {

        // given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));
        when(planRepository.findById(1L)).thenReturn(Optional.empty());
        when(taskRepository.findAllByMemberIdAndPlanId(1L, 1L))
                .thenReturn(List.of(task1, task2));

        // when & then
        assertThrows(PlanHandler.class, () -> planQueryService.getPlan(1L, 1L));
    }

    @Test
    @DisplayName("플랜 단건 조회 (실패, 남의 플랜)")
    public void getPlanTest_NotMyPlan_Fail() {

        // given (로그인한 유저의 아이디 : 1L)
        planInProgress1 = Plan.builder()
                .title("1")
                .motivation("다짐문장")
                .icon("아이콘")
                .planStatus(PlanStatus.IN_PROGRESS)
                .member(member2)
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));
        when(planRepository.findById(1L)).thenReturn(Optional.of(planInProgress1));

        // when & then
        assertThrows(PlanHandler.class, () -> planQueryService.getPlan(1L, 1L));
    }


/*------------------------------ 플랜 목록 조회 ------------------------------*/

    @Test
    @DisplayName("플랜 목록 조회 (성공, 진행중인 플랜)")
    public void getPlansByPlanStatus_InProgress_Success() {

        // given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));
        when(planRepository.findAllByMemberIdAndPlanStatus(1L, PlanStatus.IN_PROGRESS))
                .thenReturn(List.of(planInProgress1, planInProgress2));

        // when
        PlanResponseDTO.PlanListDTO result = planQueryService.getPlansByPlanStatus(1L, PlanStatus.IN_PROGRESS);

        // then
        assertNotNull(result);
        assertThat(result.getPlanStatus()).isEqualTo(PlanStatus.IN_PROGRESS);
        assertThat(result.getPlans().get(0).getPlanId()).isEqualTo(1L);
        assertThat(result.getPlans().get(0).getDDay()).isEqualTo("D-Day");
        assertThat(result.getPlans().get(1).getPlanId()).isEqualTo(2L);
        assertThat(result.getPlans().get(1).getDDay()).isEqualTo("D-1");
    }

    @Test
    @DisplayName("플랜 목록 조회 (성공, 중단한 플랜)")
    public void getPlansByPlanStatus_Paused_Success() {

        // given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));
        when(planRepository.findAllByMemberIdAndPlanStatus(1L, PlanStatus.PAUSED))
                .thenReturn(List.of(pausedPlan1, pausedPlan2));

        // when
        PlanResponseDTO.PlanListDTO result = planQueryService.getPlansByPlanStatus(1L, PlanStatus.PAUSED);

        // then
        assertNotNull(result);
        assertThat(result.getPlanStatus()).isEqualTo(PlanStatus.PAUSED);
        assertThat(result.getPlans().get(0).getPlanId()).isEqualTo(3L);
        assertThat(result.getPlans().get(0).getDDay()).isEqualTo("D-Day");
        assertThat(result.getPlans().get(1).getPlanId()).isEqualTo(4L);
        assertThat(result.getPlans().get(1).getDDay()).isEqualTo("D-1");
    }

    @Test
    @DisplayName("플랜 목록 조회 (성공, 아카이빙한 플랜)")
    public void getPlansByPlanStatus_Archived_Success() {

        // given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));
        when(planRepository.findAllByMemberIdAndPlanStatus(1L, PlanStatus.ARCHIVED))
                .thenReturn(List.of(archivedPlan1, archivedPlan2));

        // when
        PlanResponseDTO.PlanListDTO result = planQueryService.getPlansByPlanStatus(1L, PlanStatus.ARCHIVED);

        // then
        assertNotNull(result);
        assertThat(result.getPlanStatus()).isEqualTo(PlanStatus.ARCHIVED);
        assertThat(result.getPlans().get(0).getPlanId()).isEqualTo(5L);
        assertThat(result.getPlans().get(0).getDDay()).isEqualTo("D-Day");
        assertThat(result.getPlans().get(1).getPlanId()).isEqualTo(6L);
        assertThat(result.getPlans().get(1).getDDay()).isEqualTo("D+1");
    }
}