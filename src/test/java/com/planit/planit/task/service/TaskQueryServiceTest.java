package com.planit.planit.task.service;

import com.planit.planit.common.api.task.TaskHandler;
import com.planit.planit.member.Member;
import com.planit.planit.member.enums.Role;
import com.planit.planit.member.enums.SignType;
import com.planit.planit.member.repository.MemberRepository;
import com.planit.planit.plan.Plan;
import com.planit.planit.plan.enums.PlanStatus;
import com.planit.planit.plan.repository.PlanRepository;
import com.planit.planit.task.Task;
import com.planit.planit.task.converter.RoutineConverter;
import com.planit.planit.task.enums.TaskType;
import com.planit.planit.task.repository.TaskRepository;
import com.planit.planit.web.dto.task.TaskResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TaskQueryServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskQueryServiceImpl taskQueryService;

    private Member member1;
    private Member member2;
    private Plan plan;
    private Task task;

/*------------------------------ SETUP ------------------------------*/

    @BeforeEach
    void setUp() {
        initMember();                   // 회원 더미데이터 생성
        initPlan();                     // 플랜 더미데이터 생성

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
    }

    private void initMember() {
        member1 = Member.builder()
                .id(1L)
                .email("xxx@email.com")
                .password("password")
                .guiltyFreeMode(false)
                .memberName("xxx")
                .role(Role.USER)
                .signType(SignType.GOOGLE)
                .build();
        member2 = Member.builder()
                .id(2L)
                .email("yyy@email.com")
                .password("password")
                .guiltyFreeMode(false)
                .memberName("yyy")
                .role(Role.USER)
                .signType(SignType.GOOGLE)
                .build();
    }

    private void initPlan() {
        plan = Plan.builder()
                .id(1L)
                .title("1")
                .motivation("다짐문장")
                .icon("아이콘")
                .planStatus(PlanStatus.IN_PROGRESS)
                .member(member1)
                .build();
    }

/*------------------------------ 루틴 조회 ------------------------------*/

    @Test
    @DisplayName("루틴 조회 (성공)")
    void getCurrentRoutine_Success() {

        // given
        task = Task.builder()
                .id(1L)
                .title("작업1")
                .member(member1)
                .plan(plan)
                .build();

        task.setRoutine(
                TaskType.PASSIONATE,
                RoutineConverter.routineDaysToByte(List.of(DayOfWeek.FRIDAY)),
                LocalTime.of(19, 0)
        );

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // when
        TaskResponseDTO.TaskRoutineDTO result = taskQueryService.getCurrentRoutine(1L, 1L);

        // then
        assertNotNull(result);
        assertThat(result.getTaskId()).isEqualTo(1L);
        assertThat(result.getTaskType()).isEqualTo(TaskType.PASSIONATE);
        assertThat(result.getRoutineDay()).isEqualTo(List.of(DayOfWeek.FRIDAY));
        assertThat(result.getRoutineTime()).isEqualTo(LocalTime.of(19, 0));
    }

    @Test
    @DisplayName("루틴 조회 - 존재하지 않는 작업 (실패)")
    void getCurrentRoutine_NoTask_Fail() {

        // given
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(TaskHandler.class, () -> taskQueryService.getCurrentRoutine(1L, 1L));
    }

    @Test
    @DisplayName("루틴 조회 - 사용자의 작업이 아님 (실패)")
    void getCurrentRoutine_NotMyTask_Fail() {

        // given
        task = Task.builder()
                .id(1L)
                .title("작업1")
                .member(member1)
                .plan(plan)
                .build();

        task.setRoutine(
                TaskType.PASSIONATE,
                RoutineConverter.routineDaysToByte(List.of(DayOfWeek.FRIDAY)),
                LocalTime.of(19, 0)
        );

        when(memberRepository.findById(2L)).thenReturn(Optional.of(member2));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // when
        assertThrows(TaskHandler.class, () -> taskQueryService.getCurrentRoutine(2L, 1L));
    }
}