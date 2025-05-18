package com.planit.planit.member;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.planit.planit.common.entity.BaseEntity;
import com.planit.planit.dream.Dream;
import com.planit.planit.member.association.GuiltyFree;
import com.planit.planit.member.association.Term;
import com.planit.planit.member.enums.DailyCondition;
import com.planit.planit.member.enums.SignType;
import com.planit.planit.plan.Plan;
import com.planit.planit.task.Task;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 15)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column
    private SignType signType;

    @Column(nullable = false)
    private Boolean guiltyFreeMode;

    @Enumerated(EnumType.STRING)
    @Column
    private DailyCondition dailyCondition;

    @Column
    private LocalDateTime inactive;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private GuiltyFree guiltyFree;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Term term;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Plan> plans;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Task> tasks;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Dream> dreams;

/*------------------------------ CONSTRUCTOR ------------------------------*/

    protected Member() {}

    @Builder
    public Member(
            Long id,
            String email,
            String password,
            SignType signType,
            Boolean guiltyFreeMode,
            DailyCondition dailyCondition
    ) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.signType = signType;
        this.guiltyFreeMode = guiltyFreeMode;
        this.dailyCondition = dailyCondition;
    }

/*------------------------------ METHOD ------------------------------*/

    public void inactivate() {
        this.inactive = LocalDateTime.now();
    }
}
