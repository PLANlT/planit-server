package com.planit.planit.task;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.planit.planit.common.entity.BaseEntity;
import com.planit.planit.member.Member;
import com.planit.planit.plan.Plan;
import com.planit.planit.task.association.CompletedTask;
import com.planit.planit.task.enums.TaskType;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
    private Byte routine;

    @Column
    private LocalTime routineTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType taskType;

    @Column
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CompletedTask> completedTasks;

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
    private Task(
            Long id,
            String title,
            TaskType taskType,
            Member member,
            Plan plan
    ) {
        this.id = id;
        this.title = title;
        this.taskType = taskType;
        this.routine = 0;
        this.member = member;
        this.plan = plan;
        this.completedTasks = new ArrayList<>();
    }

/*------------------------------ METHOD ------------------------------*/

    public void updateTaskTitle(String title) {
        this.title = title;
    }

    public void setRoutine(
            TaskType taskType,
            Byte routine,
            @Nullable LocalTime routineTime
    ) {
        this.taskType = taskType;
        this.routine = routine;
        this.routineTime = routineTime;
    }

    public void addCompletedTask(CompletedTask completedTask) {
        this.completedTasks.add(completedTask);
    }

    public void deleteTask() {
        this.deletedAt = LocalDateTime.now();
    }

    public void deleteCompletedTasks(List<CompletedTask> completedTasks) {
        completedTasks.forEach(CompletedTask::setIsDeletedFalse);
    }
}
