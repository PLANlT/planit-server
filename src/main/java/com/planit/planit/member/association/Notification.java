package com.planit.planit.member.association;

import com.planit.planit.member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    private Long memberId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private boolean dailyTaskEnabled;

    @Column(nullable = false)
    private boolean guiltyFreeEnabled;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private Notification(Member member) {
        Assert.notNull(member, "member must not be null");
        this.member = member;
        this.dailyTaskEnabled = true;
        this.guiltyFreeEnabled = true;
    }

    public static Notification of(Member member) {
        Notification notification = new Notification(member);
        notification.updatedAt = LocalDateTime.now();
        return notification;
    }

    public void updateDailyTask(boolean enabled) {
        this.dailyTaskEnabled = enabled;
    }

    public void updateGuiltFree(boolean enabled) {
        this.guiltyFreeEnabled = enabled;
    }
}