package com.planit.planit.plan;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.planit.planit.common.api.plan.PlanHandler;
import com.planit.planit.common.api.plan.status.PlanErrorStatus;
import com.planit.planit.common.entity.BaseEntity;
import com.planit.planit.member.Member;
import com.planit.planit.plan.enums.PlanStatus;
import com.planit.planit.task.Task;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class Plan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, length = 20)
    private String title;               // 플랜 제목

    @Column(length = 40)
    private String motivation;          // 다짐 문장

    @Column(nullable = false, columnDefinition = "text")
    private String icon;                // 아이콘

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanStatus planStatus;      // 플랜 진행 여부

    @Column
    private LocalDate startedAt;        // 플랜 시작일

    @Column
    private LocalDate finishedAt;       // 플랜 종료일

    @Column
    private LocalDateTime inactive;     // 플랜 비활성화(중단, 아카이빙, 삭제)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @JsonBackReference
    private Member member;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Task> tasks;

/*------------------------------ CONSTRUCTOR ------------------------------*/

    protected Plan() {}

    @Builder(access = AccessLevel.PRIVATE)
    private Plan(Long id, String title, String motivation, String icon,
                 PlanStatus planStatus, LocalDate startedAt, LocalDate finishedAt, Member member
    ) {
        validate(member, title, icon, planStatus);
        this.id = id;
        this.title = title;
        this.motivation = motivation;
        this.icon = icon;
        this.planStatus = planStatus;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.member = member;
        this.tasks = new ArrayList<>();
    }

    public static Plan of(Long id, String title, String motivation, String icon,
                          PlanStatus planStatus, LocalDate startedAt, LocalDate finishedAt, Member member
    ) {
        return Plan.builder()
                .id(id)
                .title(title)
                .motivation(motivation)
                .icon(icon)
                .planStatus(planStatus)
                .startedAt(startedAt)
                .finishedAt(finishedAt)
                .member(member)
                .build();
    }

    public static Plan of(String title, String motivation, String icon, PlanStatus planStatus,
                          LocalDate startedAt, LocalDate finishedAt, Member member
    ) {
        return of(null, title,  motivation, icon, planStatus, startedAt, finishedAt, member);
    }

    /*------------------------------ METHOD ------------------------------*/

    public void updatePlan(
            String title,          String motivation,       String icon,
            PlanStatus planStatus, LocalDate startedAt,     LocalDate finishedAt
    ) {
        this.title = title;
        this.motivation = motivation;
        this.icon = icon;
        this.planStatus = planStatus;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }

    public void completePlan() {
        validatePlanInProgress();
        this.planStatus = PlanStatus.ARCHIVED;
        this.inactive = LocalDateTime.now();
    }

    public void pausePlan() {
        validatePlanInProgress();
        this.planStatus = PlanStatus.PAUSED;
        this.inactive = LocalDateTime.now();
    }

    public void deletePlan() {
        validateDeletablePlan();
        this.planStatus = PlanStatus.DELETED;
        this.inactive = LocalDateTime.now();
    }

    public void restartArchive() {
        validateArchive();
        this.planStatus = PlanStatus.IN_PROGRESS;
        this.inactive = null;
    }

    private void validateArchive() {
        if (!this.planStatus.equals(PlanStatus.ARCHIVED)) {
            throw new PlanHandler(PlanErrorStatus.PLAN_NOT_ARCHIVED);
        }
    }

    public int countTasks() {
        return this.tasks.stream().filter(task -> task.getDeletedAt() == null).toList().size();
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    private void validate(Member member, String title, String icon, PlanStatus planStatus) {
        Assert.notNull(member, "Member must not be null");
        Assert.notNull(title, "Title must not be null");
        Assert.notNull(icon, "Icon must not be null");
        Assert.notNull(planStatus, "PlanStatus must not be null");
    }

    private void validatePlanInProgress() {
        if (!this.planStatus.equals(PlanStatus.IN_PROGRESS)) {
            throw new PlanHandler(PlanErrorStatus.PLAN_NOT_IN_PROGRESS);
        }
    }

    private void validateDeletablePlan() {
        if (this.planStatus.equals(PlanStatus.DELETED)) {
            throw new PlanHandler(PlanErrorStatus.PLAN_ALREADY_DELETED);
        }
    }
}
