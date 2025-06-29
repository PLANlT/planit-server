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
    private Long memberId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column
    private GuiltyFreeReason reason;

    @Column
    private LocalDate active;

/*------------------------------ CONSTRUCTOR ------------------------------*/

    protected GuiltyFree() {}

    @Builder
    public GuiltyFree(
            Member member,
            GuiltyFreeReason reason,
            @Nullable LocalDate active
    ) {
        this.memberId = member.getId();
        this.member = member;
        this.reason = reason;
        this.active = active;
    }

/*------------------------------ METHOD ------------------------------*/

    public void activate(GuiltyFreeReason reason, LocalDate active) {
        this.reason = reason;
        this.active = active;
    }
}
