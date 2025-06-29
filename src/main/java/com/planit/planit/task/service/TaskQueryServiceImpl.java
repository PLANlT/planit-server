package com.planit.planit.task.service;

import com.planit.planit.common.api.member.MemberHandler;
import com.planit.planit.common.api.member.status.MemberErrorStatus;
import com.planit.planit.common.api.task.TaskHandler;
import com.planit.planit.common.api.task.status.TaskErrorStatus;
import com.planit.planit.member.Member;
import com.planit.planit.member.MemberRepository;
import com.planit.planit.plan.repository.PlanRepository;
import com.planit.planit.task.Task;
import com.planit.planit.task.repository.TaskRepository;
import com.planit.planit.web.dto.task.TaskResponseDTO;
import com.planit.planit.web.dto.task.converter.TaskConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskQueryServiceImpl implements TaskQueryService {

    private final MemberRepository memberRepository;
    private final TaskRepository taskRepository;

    @Override
    public TaskResponseDTO.TaskRoutineDTO getCurrentRoutine(Long memberId, Long taskId) {

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

        return TaskConverter.toTaskRoutineDTO(task);
    }
}
