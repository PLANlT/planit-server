package com.planit.planit.member.association;

import com.planit.planit.member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Getter
@Entity
public class Term {

    @Id
    private Long id; // 식별자 필드

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDateTime termOfUse;

    @Column(nullable = false)
    private LocalDateTime termOfPrivacy;

    @Column(nullable = false)
    private LocalDateTime termOfInfo;

    @Column(nullable = false)
    private LocalDateTime overFourteen;

    /*------------------------------ CONSTRUCTOR ------------------------------*/

    protected Term() {}

    @Builder
    private Term(
            Member member,
            LocalDateTime termOfUse,
            LocalDateTime termOfPrivacy,
            LocalDateTime termOfInfo,
            LocalDateTime overFourteen
    ) {
        validate(member, termOfUse, termOfPrivacy,  termOfInfo, overFourteen);
        this.member = member;
        this.termOfUse = termOfUse;
        this.termOfPrivacy = termOfPrivacy;
        this.termOfInfo = termOfInfo;
        this.overFourteen = overFourteen;
    }

    private void validate(Member member, LocalDateTime termOfUse, LocalDateTime termOfPrivacy,
                          LocalDateTime termOfInfo, LocalDateTime overFourteen
    ) {
        Assert.notNull(member, "member must not be null");
        Assert.notNull(member.getId(), "memberId must not be null");
        Assert.notNull(termOfUse, "termOfUse must not be null");
        Assert.notNull(termOfPrivacy, "termOfPrivacy must not be null");
        Assert.notNull(termOfInfo, "termOfInfo must not be null");
        Assert.notNull(overFourteen, "overFourteen must not be null");
    }
}
