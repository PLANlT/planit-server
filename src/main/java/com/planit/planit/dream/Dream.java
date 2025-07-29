package com.planit.planit.dream;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.planit.planit.common.entity.BaseEntity;
import com.planit.planit.member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
public class Dream extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Column
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @JsonBackReference
    private Member member;

/*------------------------------ CONSTRUCTOR ------------------------------*/

    protected Dream() {}

    @Builder
    private Dream(
            Long id,
            String content,
            Member member
    ) {
        this.id = id;
        this.content = content;
        this.member = member;
    }

/*------------------------------ METHOD ------------------------------*/

    public void deleteDream() {
        this.deletedAt = LocalDateTime.now();
    }
}
