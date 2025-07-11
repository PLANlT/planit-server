package com.planit.planit.member.association;

import com.planit.planit.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    private Long memberId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    @Builder.Default
    private boolean dailyTaskEnabled = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean guiltyFreeEnabled = true;

    private LocalDateTime updatedAt;

    public static Notification of(Member member) {
        return Notification.builder()
                .member(member)
                .memberId(member.getId()) // MapsId는 명시적으로 ID를 넣어야 함
                .build();
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