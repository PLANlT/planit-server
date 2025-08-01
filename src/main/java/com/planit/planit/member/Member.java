package com.planit.planit.member;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.planit.planit.common.entity.BaseEntity;
import com.planit.planit.dream.Dream;
import com.planit.planit.member.association.FcmToken;
import com.planit.planit.member.association.GuiltyFree;
import com.planit.planit.member.association.GuiltyFreeProperty;
import com.planit.planit.member.association.Notification;
import com.planit.planit.member.association.Term;
import com.planit.planit.member.enums.DailyCondition;
import com.planit.planit.member.enums.SignType;
import com.planit.planit.plan.Plan;
import com.planit.planit.task.Task;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.planit.planit.member.enums.Role;
import lombok.Setter;
import org.springframework.util.Assert;

@Getter
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "uk_email_inactive", columnNames = {"email", "inactive"})
})
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false)
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

    @Column(nullable = false)
    private LocalDate lastAttendanceDate;               // 최근 출석일

    @Column(nullable = false)
    private LocalDate attendanceStartedAt;              // 연속 출석 시작일

    @Column(nullable = false)
    private LocalDate lastGuiltyFreeDate;               // 최근 길티프리 실행일

    @Column(nullable = false)
    private Long maxConsecutiveDays;                    // 연속일 최고기록

    @Column
    private LocalDateTime inactive;

    @Column(nullable = false)
    private String memberName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GuiltyFree> guiltyFrees;

    @Setter
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

    @Column(nullable = false)
    private boolean isSignUpCompleted;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    @Setter
    private Notification notification;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    @Setter
    private FcmToken fcmToken;

/*------------------------------ CONSTRUCTOR ------------------------------*/

    protected Member() {}

    @Builder
    private Member(
            Long id,
            String email,
            String password,
            SignType signType,
            Boolean guiltyFreeMode,
            DailyCondition dailyCondition,
            String memberName,
            Role role
    ) {
        validate(email, password, signType, guiltyFreeMode, role);
        this.id = id;
        this.email = email;
        this.password = password;
        this.signType = signType;
        this.memberName = (memberName != null) ? memberName : "여행자";
        this.role = role;
        this.guiltyFreeMode = guiltyFreeMode;
        this.dailyCondition = dailyCondition;
        this.lastAttendanceDate = GuiltyFreeProperty.guiltyFreeInitDate;
        this.attendanceStartedAt = GuiltyFreeProperty.guiltyFreeInitDate;
        this.lastGuiltyFreeDate = GuiltyFreeProperty.guiltyFreeInitDate;
        this.maxConsecutiveDays = 0L;
        this.isSignUpCompleted = false;
        this.guiltyFrees = new ArrayList<>();
        this.plans = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.dreams = new ArrayList<>();
    }

    /*------------------------------ METHOD ------------------------------*/

    private void validate(String email, String password, SignType signType, Boolean guiltyFreeMode, Role role) {
        Assert.notNull(email, "Email is required");
        Assert.notNull(password, "Password is required");
        Assert.notNull(signType, "SignType is required");
        Assert.notNull(guiltyFreeMode, "GuiltyFreeMode is required");
        Assert.notNull(role, "Role is required");
    }

    public void inactivate() {
        this.inactive = LocalDateTime.now();
    }

    // 테스트 전용: inactive 값을 임의로 세팅
    public void setInactiveForTest(LocalDateTime inactive) {
        this.inactive = inactive;
    }

    public void activateGuiltyFree(GuiltyFree guiltyFree) {

        // 길티프리 활성화
        lastGuiltyFreeDate = guiltyFree.getActive();
        guiltyFrees.add(guiltyFree);

        // 최대 연속일을 연장해야 하는 경우
        long consecutiveDays = ChronoUnit.DAYS.between(attendanceStartedAt, lastGuiltyFreeDate);
        if (isConsecutiveAttendance(lastGuiltyFreeDate) && consecutiveDays == maxConsecutiveDays) {
            maxConsecutiveDays = maxConsecutiveDays + 1;
        }
    }

    public void updateConsecutiveDays(LocalDate today) {

        // 최초 출석인 경우
        if (lastAttendanceDate.equals(GuiltyFreeProperty.guiltyFreeInitDate) ||
            attendanceStartedAt.equals(GuiltyFreeProperty.guiltyFreeInitDate)
        ) {
            lastAttendanceDate = today;
            attendanceStartedAt = today;
            maxConsecutiveDays = 1L;
            return ;
        }

        // 연속 출석인 경우
        if (isConsecutiveAttendance(today)) {
            // 현재까지의 기록이 최대 연속일인 경우 갱신
            long consecutiveDays = ChronoUnit.DAYS.between(attendanceStartedAt, today);
            if (consecutiveDays == maxConsecutiveDays) {
                maxConsecutiveDays = maxConsecutiveDays + 1;
            }
        } else {
            // 연속 출석 시작일을 오늘로 설정
            attendanceStartedAt = today;
        }

        // 최근 출석일을 오늘로 업데이트
        lastAttendanceDate = today;
    }

    public boolean isConsecutiveAttendance(LocalDate today) {
        // 길티프리를 사용한 적이 있는 경우 길티프리 날짜 확인
        if (!lastGuiltyFreeDate.equals(GuiltyFreeProperty.guiltyFreeInitDate)) {
            return lastAttendanceDate.plusDays(1).equals(today) || (
                    lastAttendanceDate.plusDays(2).equals(today) &&
                    lastGuiltyFreeDate.plusDays(1).equals(today));
        }
        return lastAttendanceDate.plusDays(1).equals(today);
    }

    public void completeSignUp() {
        this.isSignUpCompleted = true;
    }

    public void addPlan(Plan plan) {
        this.plans.add(plan);
    }

    public void addGuiltyFree(GuiltyFree guiltyFree) {
        this.guiltyFrees.add(guiltyFree);
    }

}
