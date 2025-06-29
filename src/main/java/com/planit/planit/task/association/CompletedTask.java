package com.planit.planit.task.association;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.planit.planit.task.Task;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Entity
public class CompletedTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false)
    private LocalDate completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    @JsonBackReference
    private Task task;

    @Column
    private Boolean isDeleted;

/*------------------------------ CONSTRUCTOR ------------------------------*/

    protected CompletedTask() {}

    @Builder
    public CompletedTask(Task task, LocalDate completedAt) {
        this.task = task;
        this.completedAt = completedAt;
        this.isDeleted = false;
    }

/*------------------------------ METHOD ------------------------------*/

    public void setIsDeletedFalse() {
        this.isDeleted = true;
    }

    public void setIsDeletedTrue() {
        this.isDeleted = false;
    }
}
