package com.planit.planit.member.association;

import com.planit.planit.common.entity.BaseEntity;
import com.planit.planit.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class FcmToken extends BaseEntity {

    @Id
    private Long memberId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = true)
    private String token;

    @Column(nullable = true)
    private LocalDateTime lastUsedAt;

    public void updateLastUsedAt() {
        this.lastUsedAt = LocalDateTime.now();
    }


    public static FcmToken of(Member member, String token) {
        FcmToken fcmToken = new FcmToken();
        fcmToken.member = member;
        fcmToken.memberId = member.getId();
        fcmToken.token = token;
        fcmToken.lastUsedAt = LocalDateTime.now();
        return fcmToken;
    }

}
