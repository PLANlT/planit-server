package com.planit.planit.web.dto.member;


import com.planit.planit.member.Member;
import com.planit.planit.member.enums.SignType;
import lombok.Builder;
import lombok.Getter;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

@Getter
public class MemberInfoResponseDTO {
    private Long id;
    private String email;
    private String name;
    private String signType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    @Builder
    public MemberInfoResponseDTO(Long id, String email, String name, SignType signType, LocalDate createdAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.signType = String.valueOf(signType);
        this.createdAt = createdAt;
    }

    public static MemberInfoResponseDTO of(Member member) {
        return MemberInfoResponseDTO.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getMemberName())
                .signType(member.getSignType())
                .createdAt(member.getCreatedAt().toLocalDate()) // 여기 중요!
                .build();
    }
}
