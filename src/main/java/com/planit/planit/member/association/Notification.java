package com.planit.planit.member.association;

import com.planit.planit.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    private Long memberId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private boolean dailyTaskEnabled = true;    //TODO: 기본값 true 인지 확인

    @Column(nullable = false)
    private boolean guiltyFreeEnabled = true;

    private LocalDateTime updatedAt;

    @Builder
    public Notification(Member member) {
        this.member = member;
        this.updatedAt = LocalDateTime.now();
    }

    public static Notification of(Member member) {
        return Notification.builder()
                .member(member)
                .build();
    }

    public void updateSettings(boolean daily, boolean guilty) {
        this.dailyTaskEnabled = daily;
        this.guiltyFreeEnabled = guilty;
        this.updatedAt = LocalDateTime.now();
    }
}
