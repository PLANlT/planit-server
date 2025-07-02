package com.planit.planit.task.service;

import com.planit.planit.common.api.member.MemberHandler;
import com.planit.planit.common.api.member.status.MemberErrorStatus;
import com.planit.planit.common.api.plan.PlanHandler;
import com.planit.planit.common.api.plan.status.PlanErrorStatus;
import com.planit.planit.common.api.task.TaskHandler;
import com.planit.planit.common.api.task.status.TaskErrorStatus;
import com.planit.planit.member.Member;
import com.planit.planit.member.MemberRepository;
import com.planit.planit.plan.Plan;
import com.planit.planit.plan.repository.PlanRepository;
import com.planit.planit.task.Task;
import com.planit.planit.task.association.CompletedTask;
import com.planit.planit.task.repository.CompletedTaskRepository;
import com.planit.planit.task.repository.TaskRepository;
import com.planit.planit.web.dto.task.TaskRequestDTO;
import com.planit.planit.web.dto.task.TaskResponseDTO;
import com.planit.planit.web.dto.task.converter.TaskConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskCommandServiceImpl implements TaskCommandService {

    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;
    private final TaskRepository taskRepository;
    private final CompletedTaskRepository completedTaskRepository;

    @Override
    public TaskResponseDTO.TaskPreviewDTO createTask(Long memberId, Long planId, String title) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new PlanHandler(PlanErrorStatus.PLAN_NOT_FOUND));

        // 로그인한 회원의 플랜인지 확인
        if (!plan.getMember().getId().equals(member.getId())) {
            throw new PlanHandler(PlanErrorStatus.MEMBER_PLAN_NOT_FOUND);
        }

        // 새로운 작업 생성
        Task task = Task.builder()
                .title(title)
                .member(member)
                .plan(plan)
                .build();

        task = taskRepository.save(task);
        return TaskConverter.toTaskPreviewDTO(task);
    }

    @Override
    public TaskResponseDTO.TaskPreviewDTO updateTaskTitle(Long memberId, Long taskId, String title) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskHandler(TaskErrorStatus.TASK_NOT_FOUND));

        // 로그인한 회원의 작업인지 확인
        if (!task.getPlan().getMember().getId().equals(member.getId())) {
            throw new TaskHandler(TaskErrorStatus.MEMBER_TASK_NOT_FOUND);
        }

        // 삭제된 작업인지 확인
        if (task.getDeletedAt() != null) {
            throw new TaskHandler(TaskErrorStatus.TASK_DELETED);
        }

        // 작업명 업데이트
        task.updateTaskTitle(title);

        return TaskConverter.toTaskPreviewDTO(task);
    }

    @Override
    public TaskResponseDTO.TaskRoutineDTO setRoutine(Long memberId, Long taskId, TaskRequestDTO.RoutineDTO routineDTO) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskHandler(TaskErrorStatus.TASK_NOT_FOUND));

        // 로그인한 회원의 작업인지 확인
        if (!task.getPlan().getMember().getId().equals(member.getId())) {
            throw new TaskHandler(TaskErrorStatus.MEMBER_TASK_NOT_FOUND);
        }

        // 삭제된 작업인지 확인
        if (task.getDeletedAt() != null) {
            throw new TaskHandler(TaskErrorStatus.TASK_DELETED);
        }

        // 루틴 설정
        task.setRoutine(
                routineDTO.getTaskType(),
                routineDTO.getRoutineDay(),
                routineDTO.getRoutineTime()
        );

        return TaskConverter.toTaskRoutineDTO(task);
    }

    @Override
    public TaskResponseDTO.TaskPreviewDTO deleteTask(Long memberId, Long taskId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskHandler(TaskErrorStatus.TASK_NOT_FOUND));

        // 로그인한 회원의 작업인지 확인
        if (!task.getPlan().getMember().getId().equals(member.getId())) {
            throw new TaskHandler(TaskErrorStatus.MEMBER_TASK_NOT_FOUND);
        }

        // 삭제된 작업인지 확인
        if (task.getDeletedAt() != null) {
            throw new TaskHandler(TaskErrorStatus.TASK_DELETED);
        }

        // 작업 삭제
        task.deleteTask();

        return TaskConverter.toTaskPreviewDTO(task);
    }

    @Override
    public TaskResponseDTO.CompletedTaskDTO completeTask(Long memberId, Long taskId, LocalDate today) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskHandler(TaskErrorStatus.TASK_NOT_FOUND));

        // 로그인한 회원의 작업인지 확인
        if (!task.getPlan().getMember().getId().equals(member.getId())) {
            throw new TaskHandler(TaskErrorStatus.MEMBER_TASK_NOT_FOUND);
        }

        // 삭제된 작업인지 확인
        if (task.getDeletedAt() != null) {
            throw new TaskHandler(TaskErrorStatus.TASK_DELETED);
        }

        // 오늘 루틴에 해당하는 작업인지 확인
        if (!task.getRoutineDay().equals(today.getDayOfWeek())) {
            throw new TaskHandler(TaskErrorStatus.NOT_ROUTINE_OF_TODAY);
        }

        // 작업 완료 처리
        CompletedTask completedTask = createCompletedTask(today, task, member);

        return TaskConverter.toCompletedTaskDTO(completedTask, true);
    }

    private CompletedTask createCompletedTask(LocalDate today, Task task, Member member) {

        // 오늘 완료되거나 완료 취소된 작업 목록 조회
        List<CompletedTask> completedTasks = task.getCompletedTasks().stream()
                .filter(completedTask -> completedTask.getTask().getId().equals(task.getId()))      // 선택된 작업
                .filter(completedTask ->                                                            // 오늘 작업
                        completedTask.getCompletedAt().equals(today))
                .toList();

        if (!completedTasks.isEmpty()) {
            // 완료 취소된 작업 -> 작업을 완료 처리하여 return
            // 이미 완료된 작업 -> 완료 처리 불가하므로 throw
            if (completedTasks.get(0).getIsDeleted()) {
                completedTasks.get(0).setIsDeletedTrue();
                return completedTasks.get(0);
            }
            throw new TaskHandler(TaskErrorStatus.TASK_ALREADY_COMPLETED);
        }

        // 완료 정보가 없는 경우 새로 저장
        CompletedTask completedTask = CompletedTask.builder()
                .task(task)
                .completedAt(today)
                .build();

        completedTaskRepository.save(completedTask);
        task.addCompletedTask(completedTask);

        // 연속일 정보 업데이트
        member.updateConsecutiveDays(today);

        return completedTask;
    }

    @Override
    public TaskResponseDTO.CompletedTaskDTO cancelTaskCompletion(Long memberId, Long taskId, LocalDate today) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskHandler(TaskErrorStatus.TASK_NOT_FOUND));

        // 로그인한 회원의 작업인지 확인
        if (!task.getPlan().getMember().getId().equals(member.getId())) {
            throw new TaskHandler(TaskErrorStatus.MEMBER_TASK_NOT_FOUND);
        }

        // 삭제된 작업인지 확인
        if (task.getDeletedAt() != null) {
            throw new TaskHandler(TaskErrorStatus.TASK_DELETED);
        }

        // 완료된 작업 정보 삭제
        CompletedTask deletedCompletedTask = deleteCompletedTask(today, task);

        return TaskConverter.toCompletedTaskDTO(deletedCompletedTask, false);
    }

    private CompletedTask deleteCompletedTask(LocalDate today, Task task) {

        // 오늘 완료된 작업 목록 조회
        List<CompletedTask> completedTasks = task.getCompletedTasks().stream()
                .filter(completedTask -> completedTask.getTask().getId().equals(task.getId()))      // 선택된 작업
                .filter(completedTask -> !completedTask.getIsDeleted())                             // 완료가 취소되지 않은 작업
                .filter(completedTask ->                                                            // 오늘 작업
                        completedTask.getCompletedAt().equals(today))
                .toList();

        // 완료된 작업이 없는 경우 삭제 불가
        if (completedTasks.isEmpty()) {
            throw new TaskHandler(TaskErrorStatus.TASK_NOT_COMPLETED);
        }

        // 완료된 작업 정보 삭제
        task.deleteCompletedTasks(completedTasks);

        return completedTasks.get(0);
    }


}
