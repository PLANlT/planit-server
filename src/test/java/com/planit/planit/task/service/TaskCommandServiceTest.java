package com.planit.planit.task.service;

import com.planit.planit.common.api.plan.PlanHandler;
import com.planit.planit.common.api.task.TaskHandler;
import com.planit.planit.member.Member;
import com.planit.planit.member.enums.Role;
import com.planit.planit.member.enums.SignType;
import com.planit.planit.member.repository.MemberRepository;
import com.planit.planit.member.association.GuiltyFree;
import com.planit.planit.member.enums.GuiltyFreeReason;
import com.planit.planit.plan.Plan;
import com.planit.planit.plan.enums.PlanStatus;
import com.planit.planit.plan.repository.PlanRepository;
import com.planit.planit.task.Task;
import com.planit.planit.task.association.CompletedTask;
import com.planit.planit.task.converter.RoutineConverter;
import com.planit.planit.task.enums.TaskType;
import com.planit.planit.task.repository.CompletedTaskRepository;
import com.planit.planit.task.repository.TaskRepository;
import com.planit.planit.web.dto.task.TaskRequestDTO;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TaskCommandServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CompletedTaskRepository completedTaskRepository;

    @InjectMocks
    private TaskCommandServiceImpl taskCommandService;

    private Member member1;
    private Member member2;
    private Plan plan;
    private Task task;
    private CompletedTask completedTask;


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
        plan = Plan.of(1L, "1", "다짐문장", "아이콘",
                PlanStatus.IN_PROGRESS, null, null, member1);
    }

/*------------------------------ 작업 생성 ------------------------------*/

    @Test
    @DisplayName("작업 생성 (성공)")
    void createTask_Success() {

        // given
        task = Task.builder()
                .id(1L)
                .title("작업1")
                .taskType(TaskType.ALL)
                .member(member1)
                .plan(plan)
                .build();

        TaskRequestDTO.TaskCreateDTO requestDTO = TaskRequestDTO.TaskCreateDTO.builder()
                .title("작업1")
                .taskType(TaskType.ALL)
                .build();

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // when
        TaskResponseDTO.TaskPreviewDTO result = taskCommandService
                .createTask(1L, 1L, requestDTO);

        // then
        assertNotNull(result);
        assertThat(result.getTaskId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("작업1");
        assertThat(result.getTaskType()).isEqualTo(TaskType.ALL);
    }

    @Test
    @DisplayName("작업 생성 - 로그인한 회원의 플랜이 아님 (실패)")
    void createTask_NotMyPlan_Fail() {

        // given
        task = Task.builder()
                .id(1L)
                .title("작업1")
                .taskType(TaskType.ALL)
                .member(member1)
                .plan(plan)
                .build();

        TaskRequestDTO.TaskCreateDTO requestDTO = TaskRequestDTO.TaskCreateDTO.builder()
                .title("작업1")
                .taskType(TaskType.ALL)
                .build();

        when(memberRepository.findById(2L)).thenReturn(Optional.of(member2));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // when & then
        assertThrows(PlanHandler.class, () ->
                taskCommandService.createTask(2L,1L, requestDTO));
    }

/*------------------------------ 작업명 수정 ------------------------------*/

    @Test
    @DisplayName("작업명 수정 (성공)")
    void updateTaskTitle_Success() {

        // given
        task = Task.builder()
                .id(1L)
                .title("작업1")
                .taskType(TaskType.ALL)
                .member(member1)
                .plan(plan)
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // when
        TaskResponseDTO.TaskPreviewDTO result = taskCommandService
                .updateTaskTitle(1L, 1L, "변경된 작업");

        // then
        assertNotNull(result);
        assertThat(result.getTaskId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("변경된 작업");
        assertThat(result.getTaskType()).isEqualTo(TaskType.ALL);
    }

    @Test
    @DisplayName("작업명 수정 - 존재하지 않는 작업 (실패)")
    void updateTaskTitle_NoTask_Fail() {

        // given
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(TaskHandler.class, () ->
                taskCommandService.updateTaskTitle(1L, 1L, "변경된 작업"));
    }

    @Test
    @DisplayName("작업명 수정 - 로그인한 회원의 작업이 아님 (실패)")
    void updateTaskTitle_NotMyTask_Fail() {

        // given
        task = Task.builder()
                .id(1L)
                .title("작업1")
                .member(member1)
                .plan(plan)
                .build();

        when(memberRepository.findById(2L)).thenReturn(Optional.of(member2));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // when & then
        assertThrows(TaskHandler.class, () ->
                taskCommandService.updateTaskTitle(2L, 1L, "변경된 작업"));
    }

/*------------------------------ 루틴 설정 ------------------------------*/

    @Test
    @DisplayName("루틴 설정 (성공)")
    void setRoutine_Success() {

        // given
        task = Task.builder()
                .id(1L)
                .title("작업1")
                .member(member1)
                .plan(plan)
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskRequestDTO.RoutineDTO routineDTO = TaskRequestDTO.RoutineDTO.builder()
                .taskType(TaskType.SLOW)
                .routineDay(List.of(DayOfWeek.SATURDAY))
                .routineTime(LocalTime.of(14, 0))
                .build();

        // when
        TaskResponseDTO.TaskRoutineDTO result = taskCommandService.setRoutine(1L, 1L, routineDTO);

        // then
        assertNotNull(result);
        assertThat(result.getTaskId()).isEqualTo(1L);
        assertThat(result.getTaskType()).isEqualTo(TaskType.SLOW);
        assertThat(result.getRoutineDay()).isEqualTo(List.of(DayOfWeek.SATURDAY));
        assertThat(result.getRoutineTime()).isEqualTo(LocalTime.of(14, 0));
    }

    @Test
    @DisplayName("루틴 설정 - 존재하지 않는 작업 (실패)")
    void setRoutine_NoTask_Fail() {

        // given
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        TaskRequestDTO.RoutineDTO routineDTO = TaskRequestDTO.RoutineDTO.builder()
                .taskType(TaskType.SLOW)
                .routineDay(List.of(DayOfWeek.SATURDAY))
                .routineTime(LocalTime.of(14, 0))
                .build();

        // when & then
        assertThrows(TaskHandler.class, () ->
                taskCommandService.setRoutine(1L, 1L, routineDTO));
    }

    @Test
    @DisplayName("루틴 설정 - 로그인한 회원의 작업이 아님 (실패)")
    void setRoutine_NotMyTask_Fail() {

        // given
        task = Task.builder()
                .id(1L)
                .title("작업1")
                .member(member1)
                .plan(plan)
                .build();

        when(memberRepository.findById(2L)).thenReturn(Optional.of(member2));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskRequestDTO.RoutineDTO routineDTO = TaskRequestDTO.RoutineDTO.builder()
                .taskType(TaskType.SLOW)
                .routineDay(List.of(DayOfWeek.SATURDAY))
                .routineTime(LocalTime.of(14, 0))
                .build();

        // when & then
        assertThrows(TaskHandler.class, () ->
                taskCommandService.setRoutine(2L, 1L, routineDTO));
    }

/*------------------------------ 작업 삭제 ------------------------------*/

    @Test
    @DisplayName("작업 삭제 (성공)")
    void deleteTask_Success() {

        // given
        task = Task.builder()
                .id(1L)
                .title("작업1")
                .member(member1)
                .plan(plan)
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // when
        TaskResponseDTO.TaskPreviewDTO result = taskCommandService.deleteTask(1L, 1L);

        // then
        assertNotNull(result);
        assertThat(result.getTaskId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("작업 삭제 - 존재하지 않는 작업 (실패)")
    void deleteTask_NoTask_Fail() {

        // given
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(TaskHandler.class, () -> taskCommandService.deleteTask(1L, 1L));
    }

    @Test
    @DisplayName("작업 삭제 - 로그인한 회원의 작업이 아님 (실패)")
    void deleteTask_NotMyTask_Fail() {

        // given
        task = Task.builder()
                .id(1L)
                .title("작업1")
                .member(member1)
                .plan(plan)
                .build();

        when(memberRepository.findById(2L)).thenReturn(Optional.of(member2));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // when & then
        assertThrows(TaskHandler.class, () -> taskCommandService.deleteTask(2L, 1L));
    }

/*------------------------------ 작업 완료 ------------------------------*/

    @Test
    @DisplayName("작업 완료 - 연속 출석한 경우 (성공)")
    void completeTask_Consecutive_Success() {

        // given
        member1.updateConsecutiveDays(LocalDate.of(2025, 1, 1));
        member1.updateConsecutiveDays(LocalDate.of(2025, 1, 2));

        task = Task.builder()
                .id(1L)
                .title("작업1")
                .member(member1)
                .plan(plan)
                .build();

        task.setRoutine(
                TaskType.ALL,
                RoutineConverter.routineDaysToByte(List.of(LocalDate.of(2025, 1, 3).getDayOfWeek())),
                null
        );

        completedTask = CompletedTask.builder()
                .task(task)
                .completedAt(LocalDate.of(2025, 1, 3))
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(completedTaskRepository.save(any(CompletedTask.class))).thenReturn(completedTask);
        when(completedTaskRepository.findAllByTaskIdAndCompletedAt(1L, LocalDate.of(2025, 1, 3)))
                .thenReturn(List.of());

        // when
        TaskResponseDTO.CompletedTaskDTO result = taskCommandService.completeTask(1L, 1L,LocalDate.of(2025, 1, 3));

        // then
        assertNotNull(result);
        assertThat(result.getTaskId()).isEqualTo(1L);
        assertThat(result.getDate()).isEqualTo(LocalDate.of(2025, 1, 3));
        assertThat(result.getIsCompleted()).isEqualTo(true);
        assertThat(member1.getLastAttendanceDate()).isEqualTo(LocalDate.of(2025, 1, 3));
        assertThat(member1.getAttendanceStartedAt()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(member1.getMaxConsecutiveDays()).isEqualTo(3L);
    }


    @Test
    @DisplayName("작업 완료 - 중간에 길티프리를 포함하여 연속 출석한 경우 (성공)")
    void completeTask_ConsecutiveWithGuiltyFree_Success() {

        // given
        member1.updateConsecutiveDays(LocalDate.of(2025, 1, 1));
        member1.activateGuiltyFree(GuiltyFree.of(
                member1,
                GuiltyFreeReason.LACK_OF_MOTIVATION,
                LocalDate.of(2025, 1, 2))
        );

        task = Task.builder()
                .id(1L)
                .title("작업1")
                .member(member1)
                .plan(plan)
                .build();

        task.setRoutine(TaskType.ALL,
                RoutineConverter.routineDaysToByte(List.of(LocalDate.of(2025, 1, 3).getDayOfWeek())),
                null
        );

        completedTask = CompletedTask.builder()
                .task(task)
                .completedAt(LocalDate.of(2025, 1, 3))
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(completedTaskRepository.save(any(CompletedTask.class))).thenReturn(completedTask);
        when(completedTaskRepository.findAllByTaskIdAndCompletedAt(1L, LocalDate.of(2025, 1, 3)))
                .thenReturn(List.of());

        // when
        TaskResponseDTO.CompletedTaskDTO result = taskCommandService.completeTask(1L, 1L,LocalDate.of(2025, 1, 3));

        // then
        assertNotNull(result);
        assertThat(result.getTaskId()).isEqualTo(1L);
        assertThat(result.getDate()).isEqualTo(LocalDate.of(2025, 1, 3));
        assertThat(result.getIsCompleted()).isEqualTo(true);
        assertThat(member1.getLastAttendanceDate()).isEqualTo(LocalDate.of(2025, 1, 3));
        assertThat(member1.getLastGuiltyFreeDate()).isEqualTo(LocalDate.of(2025, 1, 2));
        assertThat(member1.getAttendanceStartedAt()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(member1.getMaxConsecutiveDays()).isEqualTo(3L);
    }

    @Test
    @DisplayName("작업 완료 - 연속 출석이 끊긴 경우 (성공)")
    void completeTask_NotConsecutive_Success() {

        // given
        member1.updateConsecutiveDays(LocalDate.of(2025, 1, 1));
        member1.updateConsecutiveDays(LocalDate.of(2025, 1, 2));

        task = Task.builder()
                .id(1L)
                .title("작업1")
                .member(member1)
                .plan(plan)
                .build();

        task.setRoutine(
                TaskType.ALL,
                RoutineConverter.routineDaysToByte(List.of(LocalDate.of(2025, 1, 4).getDayOfWeek())),
                null
        );

        completedTask = CompletedTask.builder()
                .task(task)
                .completedAt(LocalDate.of(2025, 1, 4))
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(completedTaskRepository.save(any(CompletedTask.class))).thenReturn(completedTask);
        when(completedTaskRepository.findAllByTaskIdAndCompletedAt(1L, LocalDate.of(2025, 1, 4)))
                .thenReturn(List.of());

        // when
        TaskResponseDTO.CompletedTaskDTO result = taskCommandService.completeTask(1L, 1L,LocalDate.of(2025, 1, 4));

        // then
        assertNotNull(result);
        assertThat(result.getTaskId()).isEqualTo(1L);
        assertThat(result.getDate()).isEqualTo(LocalDate.of(2025, 1, 4));
        assertThat(result.getIsCompleted()).isEqualTo(true);
        assertThat(member1.getLastAttendanceDate()).isEqualTo(LocalDate.of(2025, 1, 4));
        assertThat(member1.getAttendanceStartedAt()).isEqualTo(LocalDate.of(2025, 1, 4));
        assertThat(member1.getMaxConsecutiveDays()).isEqualTo(2L);
    }

    @Test
    @DisplayName("작업 완료 - 중간에 길티프리를 포함하고 연속 출석이 끊긴 경우 (성공)")
    void completeTask_NotConsecutiveWithGuiltyFree_Success() {

        // given
        member1.updateConsecutiveDays(LocalDate.of(2025, 1, 1));
        member1.updateConsecutiveDays(LocalDate.of(2025, 1, 2));
        member1.activateGuiltyFree(GuiltyFree.of(
                member1,
                GuiltyFreeReason.LACK_OF_MOTIVATION,
                LocalDate.of(2025, 1, 3))
        );

        task = Task.builder()
                .id(1L)
                .title("작업1")
                .member(member1)
                .plan(plan)
                .build();

        task.setRoutine(
                TaskType.ALL,
                RoutineConverter.routineDaysToByte(List.of(LocalDate.of(2025, 1, 5).getDayOfWeek())),
                null
        );

        completedTask = CompletedTask.builder()
                .task(task)
                .completedAt(LocalDate.of(2025, 1, 5))
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(completedTaskRepository.save(any(CompletedTask.class))).thenReturn(completedTask);
        when(completedTaskRepository.findAllByTaskIdAndCompletedAt(1L, LocalDate.of(2025, 1, 5)))
                .thenReturn(List.of());

        // when
        TaskResponseDTO.CompletedTaskDTO result = taskCommandService.completeTask(1L, 1L,LocalDate.of(2025, 1, 5));

        // then
        assertNotNull(result);
        assertThat(result.getTaskId()).isEqualTo(1L);
        assertThat(result.getDate()).isEqualTo(LocalDate.of(2025, 1, 5));
        assertThat(result.getIsCompleted()).isEqualTo(true);
        assertThat(member1.getLastAttendanceDate()).isEqualTo(LocalDate.of(2025, 1, 5));
        assertThat(member1.getLastGuiltyFreeDate()).isEqualTo(LocalDate.of(2025, 1, 3));
        assertThat(member1.getAttendanceStartedAt()).isEqualTo(LocalDate.of(2025, 1, 5));
        assertThat(member1.getMaxConsecutiveDays()).isEqualTo(3L);
    }


    @Test
    @DisplayName("작업 완료 - 존재하지 않는 작업 (실패)")
    void completeTask_NoTask_Fail() {

        // given
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(TaskHandler.class, () -> taskCommandService.completeTask(1L, 1L, LocalDate.of(2025, 1, 3)));

    }

    @Test
    @DisplayName("작업 완료 - 로그인한 회원의 작업이 아님 (실패)")
    void completeTask_NotMyTask_Fail() {

        // given
        task = Task.builder()
                .id(1L)
                .title("작업1")
                .member(member1)
                .plan(plan)
                .build();

        when(memberRepository.findById(2L)).thenReturn(Optional.of(member2));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(completedTaskRepository.findAllByTaskIdAndCompletedAt(1L, LocalDate.of(2025, 1, 1)))
                .thenReturn(List.of());

        // when & then
        assertThrows(TaskHandler.class, () -> taskCommandService.completeTask(2L, 1L, LocalDate.of(2025, 1, 1)));
    }

    @Test
    @DisplayName("작업 완료 - 이미 완료된 작업 (실패)")
    void completeTask_AlreadyCompleted_Fail() {

        // given
        task = Task.builder()
                .id(1L)
                .title("작업1")
                .member(member1)
                .plan(plan)
                .build();

        task.setRoutine(
                TaskType.ALL,
                RoutineConverter.routineDaysToByte(List.of(LocalDate.of(2025, 1, 5).getDayOfWeek())),
                null);

        completedTask = CompletedTask.builder()
                .task(task)
                .completedAt(LocalDate.of(2025, 1, 1))
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        when(completedTaskRepository.findAllByTaskIdAndCompletedAt(1L, LocalDate.of(2025, 1, 1)))
                .thenReturn(List.of(completedTask));

        // when & then
        assertThrows(TaskHandler.class, () -> taskCommandService.completeTask(1L, 1L, LocalDate.of(2025, 1, 1)));
    }

/*------------------------------ 작업 완료 취소 ------------------------------*/

    @Test
    @DisplayName("작업 완료 취소 (성공)")
    void cancelTaskCompletion_Success() {

        // given
        task = Task.builder()
                .id(1L)
                .title("작업1")
                .member(member1)
                .plan(plan)
                .build();

        completedTask = CompletedTask.builder()
                .task(task)
                .completedAt(LocalDate.of(2025, 1, 1))
                .build();
        task.addCompletedTask(completedTask);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(completedTaskRepository.findAllByTaskIdAndCompletedAt(1L, LocalDate.of(2025, 1, 1)))
                .thenReturn(List.of(completedTask));

        // when
        TaskResponseDTO.CompletedTaskDTO result = taskCommandService
                .cancelTaskCompletion(1L, 1L, LocalDate.of(2025, 1, 1));

        // then
        assertNotNull(result);
        assertThat(result.getTaskId()).isEqualTo(1L);
        assertThat(result.getDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(result.getIsCompleted()).isEqualTo(false);
    }

    @Test
    @DisplayName("작업 완료 취소 - 존재하지 않는 작업 (실패)")
    void cancelTaskCompletion_NoTask_Fail() {

        // given
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(TaskHandler.class, () -> taskCommandService.cancelTaskCompletion(1L, 1L, LocalDate.of(2025, 1, 1)));
    }

    @Test
    @DisplayName("작업 완료 취소 - 로그인한 회원의 작업이 아님 (실패)")
    void cancelTaskCompletion_NotMyTask_Fail() {

        // given
        task = Task.builder()
                .id(1L)
                .title("작업1")
                .member(member1)
                .plan(plan)
                .build();

        completedTask = CompletedTask.builder()
                .task(task)
                .completedAt(LocalDate.of(2025, 1, 1))
                .build();
        task.addCompletedTask(completedTask);

        when(memberRepository.findById(2L)).thenReturn(Optional.of(member2));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(completedTaskRepository.findAllByTaskIdAndCompletedAt(1L, LocalDate.of(2025, 1, 1)))
                .thenReturn(List.of(completedTask));


        // when & then
        assertThrows(TaskHandler.class, () -> taskCommandService.cancelTaskCompletion(2L,  1L, LocalDate.of(2025, 1, 1)));
    }

    @Test
    @DisplayName("작업 완료 취소 - 완료되지 않은 작업 (실패)")
    void cancelTaskCompletion_UncompletedTask_Fail() {

        // given
        task = Task.builder()
                .id(1L)
                .title("작업1")
                .member(member1)
                .plan(plan)
                .build();

        completedTask = CompletedTask.builder()
                .task(task)
                .completedAt(LocalDate.of(2025, 1, 1))
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(completedTaskRepository.findAllByTaskIdAndCompletedAt(1L, LocalDate.of(2025, 1, 1)))
                .thenReturn(List.of());


        // when & then
        assertThrows(TaskHandler.class, () -> taskCommandService.cancelTaskCompletion(1L, 1L, LocalDate.of(2025, 1, 1)));

    }
}