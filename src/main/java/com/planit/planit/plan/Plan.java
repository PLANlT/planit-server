package com.planit.planit.plan;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.planit.planit.common.entity.BaseEntity;
import com.planit.planit.member.Member;
import com.planit.planit.task.Task;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
public class Plan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, length = 30)
    private String title;

    @Column(nullable = false, length = 100)
    private String goal;

    @Column(nullable = false, length = 100)
    private String motivation;

    @Column(nullable = false)
    private Boolean isCompleted;

    @Column
    private LocalDateTime completedAt;

    @Column
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @JsonBackReference
    private Member member;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Task> tasks;

/*------------------------------ CONSTRUCTOR ------------------------------*/

    protected Plan() {}

    @Builder
    public Plan(
            Long id,
            String title,
            String goal,
            String motivation,
            Member member

    ) {
        this.id = id;
        this.title = title;
        this.goal = goal;
        this.motivation = motivation;
        this.isCompleted = false;
        this.member = member;
    }

/*------------------------------ METHOD ------------------------------*/

    public void completePlan() {
        this.isCompleted = true;
        this.completedAt = LocalDateTime.now();
    }

    public void deletePlan() {
        this.deletedAt = LocalDateTime.now();
    }
}
