package com.planit.planit.member.association;

import com.planit.planit.member.Member;
import com.planit.planit.member.enums.GuiltyFreeReason;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

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
    private LocalDateTime active;

    @Column
    private LocalDateTime inactive;

/*------------------------------ CONSTRUCTOR ------------------------------*/

    protected GuiltyFree() {}

    @Builder
    public GuiltyFree(
            Member member,
            GuiltyFreeReason reason,
            @Nullable LocalDateTime active,
            @Nullable LocalDateTime inactive
    ) {
        this.memberId = member.getId();
        this.member = member;
        this.reason = reason;
        this.active = active;
        this.inactive = inactive;
    }

/*------------------------------ METHOD ------------------------------*/

    public void activate() {
        this.active = LocalDateTime.now();
    }

    public void inactivate() {
        this.inactive = LocalDateTime.now();
    }
}
