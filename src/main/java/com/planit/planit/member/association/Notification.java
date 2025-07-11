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

    public static Notification of(Member member) {
        Notification notification = new Notification();
        notification.setMember(member);
        notification.setMemberId(member.getId());
        notification.setDailyTaskEnabled(true);
        notification.setGuiltyFreeEnabled(true);
        notification.setUpdatedAt(LocalDateTime.now());
        return notification;
    }

    public void updateDailyTask(boolean enabled) {
        this.dailyTaskEnabled = enabled;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateGuiltFree(boolean enabled) {
        this.guiltyFreeEnabled = enabled;
        this.updatedAt = LocalDateTime.now();
    }
}
