package com.planit.planit.member.association;

import com.planit.planit.member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
public class Term {

    @Id
    private Long memberId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDateTime termOfUse;

    @Column(nullable = false)
    private LocalDateTime termOfPrivacy;

/*------------------------------ CONSTRUCTOR ------------------------------*/

    protected Term() {}

    @Builder
    public Term(
            Member member,
            LocalDateTime termOfUse,
            LocalDateTime termOfPrivacy
    ) {
        this.memberId = member.getId();
        this.member = member;
        this.termOfUse = termOfUse;
        this.termOfPrivacy = termOfPrivacy;
    }
}
