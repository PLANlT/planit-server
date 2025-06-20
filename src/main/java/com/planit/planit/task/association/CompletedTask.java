package com.planit.planit.task.association;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.planit.planit.common.entity.BaseEntity;
import com.planit.planit.task.Task;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class CompletedTask extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    @JsonBackReference
    private Task task;

/*------------------------------ CONSTRUCTOR ------------------------------*/

    protected CompletedTask() {}

    public CompletedTask(Task task) {
        this.task = task;
    }
}
