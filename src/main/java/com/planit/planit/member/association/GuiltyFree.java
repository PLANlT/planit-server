package com.planit.planit.member.association;

import com.planit.planit.member.Member;
import com.planit.planit.member.enums.GuiltyFreeReason;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.Getter;
import org.springframework.util.Assert;

import java.time.LocalDate;

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
        validate(member, reason, active);
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

    private void validate(Member member, GuiltyFreeReason reason, LocalDate active) {
        Assert.notNull(member, "member must not be null");
        Assert.notNull(reason, "reason must not be null");
        Assert.notNull(active, "active must not be null");
    }
}
