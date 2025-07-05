package com.planit.planit.member.association;

import com.planit.planit.common.entity.BaseEntity;
import com.planit.planit.member.Member;
import com.planit.planit.member.enums.GuiltyFreeReason;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
public class GuiltyFree {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column
    private GuiltyFreeReason reason;

    @Column(nullable = false)
    private LocalDate active;

/*------------------------------ CONSTRUCTOR ------------------------------*/

    protected GuiltyFree() {}

    private GuiltyFree(
            Member member,
            GuiltyFreeReason reason,
            LocalDate active
    ) {
        this.member = member;
        this.reason = reason;
        this.active = active;
    }

    public static GuiltyFree of(Member member, GuiltyFreeReason reason, LocalDate active) {
        return new GuiltyFree(member, reason, active);
    }

/*------------------------------ METHOD ------------------------------*/

    public void activate(GuiltyFreeReason reason, LocalDate active) {
        this.reason = reason;
        this.active = active;
    }
}
