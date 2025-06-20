package com.planit.planit.task;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.planit.planit.common.entity.BaseEntity;
import com.planit.planit.member.Member;
import com.planit.planit.plan.Plan;
import com.planit.planit.task.enums.RoutineDay;
import com.planit.planit.task.enums.TaskType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Entity
public class Task extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, length = 30)
    private String title;

    @Column(nullable = false)
    private Boolean isRoutine;

    @Enumerated(EnumType.STRING)
    @Column
    private RoutineDay routineDay;

    @Column
    private LocalTime routineTime;

    @Enumerated(EnumType.STRING)
    @Column
    private TaskType taskType;

    @Column(nullable = false, length = 30)
    private Boolean isCompleted;

    @Column
    private LocalDateTime completedAt;

    @Column
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @JsonBackReference
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    @JsonBackReference
    private Plan plan;

/*------------------------------ CONSTRUCTOR ------------------------------*/

    protected Task() {}

    @Builder
    public Task(
            Long id,
            String title,
            Boolean isRoutine,
            TaskType taskType,
            Member member,
            @Nullable Plan plan,
            @Nullable RoutineDay routineDay,
            @Nullable LocalTime routineTime
    ) {
        this.id = id;
        this.title = title;
        this.isRoutine = isRoutine;
        this.taskType = taskType;
        this.isCompleted = false;
        this.member = member;
        this.plan = plan;
        this.routineDay = routineDay;
        this.routineTime = routineTime;
    }

/*------------------------------ METHOD ------------------------------*/

    public void completeTask() {
        this.isCompleted = true;
        this.completedAt = LocalDateTime.now();
    }

    public void deleteTask() {
        this.deletedAt = LocalDateTime.now();
    }
}
